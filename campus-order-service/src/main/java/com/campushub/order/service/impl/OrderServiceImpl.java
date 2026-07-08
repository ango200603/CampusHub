package com.campushub.order.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.campushub.common.api.Result;
import com.campushub.common.constant.RedisKeyConstant;
import com.campushub.common.exception.BusinessException;
import com.campushub.common.exception.ErrorCode;
import com.campushub.common.mq.RabbitKeys;
import com.campushub.common.redis.RedisLock;
import com.campushub.common.util.NoGenerator;
import com.campushub.order.client.TradeClient;
import com.campushub.order.dto.CreateOrderRequest;
import com.campushub.order.entity.OrderRecord;
import com.campushub.order.enums.OrderStatus;
import com.campushub.order.mapper.OrderRecordMapper;
import com.campushub.order.service.OrderService;
import com.campushub.order.vo.OrderVO;
import com.campushub.order.vo.TradeItemVO;
import com.rabbitmq.client.Channel;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

/**
 * Order service implementation.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final OrderRecordMapper orderMapper;
    private final TradeClient tradeClient;
    private final RedisLock redisLock;
    private final RabbitTemplate rabbitTemplate;

    /**
     * {@inheritDoc}
     */
    @Override
    public OrderVO create(Long buyerId, CreateOrderRequest request) {
        return redisLock.execute(RedisKeyConstant.lockTradeItem(request.getItemId()), Duration.ofSeconds(10), () -> {
            TradeItemVO item = unwrap(tradeClient.detail(request.getItemId()));
            if (buyerId.equals(item.getSellerId())) {
                throw new BusinessException(ErrorCode.BAD_REQUEST, "不能购买自己发布的商品");
            }
            if (!"AVAILABLE".equals(item.getStatus())) {
                throw new BusinessException(ErrorCode.CONFLICT, "商品不可购买");
            }
            item = unwrap(tradeClient.lockForOrder(request.getItemId()));
            LocalDateTime now = LocalDateTime.now();
            OrderRecord order = new OrderRecord();
            order.setOrderNo(NoGenerator.orderNo());
            order.setBuyerId(buyerId);
            order.setSellerId(item.getSellerId());
            order.setItemId(item.getId());
            order.setAmount(item.getPrice());
            order.setOrderType(request.getOrderType() == null ? "TRADE_ITEM" : request.getOrderType());
            order.setStatus(OrderStatus.UNPAID.name());
            order.setCreatedAt(now);
            order.setUpdatedAt(now);
            try {
                orderMapper.insert(order);
            } catch (Exception ex) {
                tradeClient.release(request.getItemId());
                throw ex;
            }
            rabbitTemplate.convertAndSend(RabbitKeys.ORDER_EXCHANGE, RabbitKeys.ORDER_TIMEOUT_DELAY_KEY, Map.of(
                    "orderNo", order.getOrderNo(),
                    "itemId", order.getItemId(),
                    "buyerId", buyerId
            ));
            return OrderVO.from(order);
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public OrderVO get(Long userId, Long id) {
        OrderRecord order = require(id);
        if (!order.getBuyerId().equals(userId) && !order.getSellerId().equals(userId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "无权查看该订单");
        }
        return OrderVO.from(order);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<OrderVO> my(Long userId) {
        return orderMapper.selectList(Wrappers.<OrderRecord>lambdaQuery()
                        .eq(OrderRecord::getBuyerId, userId)
                        .or()
                        .eq(OrderRecord::getSellerId, userId)
                        .orderByDesc(OrderRecord::getCreatedAt))
                .stream().map(OrderVO::from).toList();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void cancel(Long userId, Long id) {
        OrderRecord order = require(id);
        if (!order.getBuyerId().equals(userId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "只能取消自己的订单");
        }
        int updated = orderMapper.update(null, Wrappers.<OrderRecord>lambdaUpdate()
                .eq(OrderRecord::getId, id)
                .eq(OrderRecord::getStatus, OrderStatus.UNPAID.name())
                .set(OrderRecord::getStatus, OrderStatus.CANCELED.name())
                .set(OrderRecord::getUpdatedAt, LocalDateTime.now()));
        if (updated == 0) {
            throw new BusinessException(ErrorCode.CONFLICT, "订单当前状态不可取消");
        }
        tradeClient.release(order.getItemId());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void handlePaySuccess(Map<String, Object> payload, Message message, Channel channel) throws IOException {
        long tag = message.getMessageProperties().getDeliveryTag();
        try {
            String orderNo = String.valueOf(payload.get("orderNo"));
            OrderRecord order = orderMapper.selectOne(Wrappers.<OrderRecord>lambdaQuery()
                    .eq(OrderRecord::getOrderNo, orderNo)
                    .last("limit 1"));
            if (order == null) {
                throw new BusinessException(ErrorCode.NOT_FOUND, "订单不存在");
            }
            if (OrderStatus.PAID.name().equals(order.getStatus())) {
                channel.basicAck(tag, false);
                return;
            }
            int updated = orderMapper.update(null, Wrappers.<OrderRecord>lambdaUpdate()
                    .eq(OrderRecord::getOrderNo, orderNo)
                    .eq(OrderRecord::getStatus, OrderStatus.UNPAID.name())
                    .set(OrderRecord::getStatus, OrderStatus.PAID.name())
                    .set(OrderRecord::getUpdatedAt, LocalDateTime.now()));
            if (updated > 0) {
                tradeClient.sold(order.getItemId());
                rabbitTemplate.convertAndSend(RabbitKeys.NOTICE_EXCHANGE, RabbitKeys.NOTICE_SEND_KEY, Map.of(
                        "userId", order.getBuyerId(),
                        "title", "支付成功",
                        "content", "订单 " + orderNo + " 已支付成功"
                ));
            }
            channel.basicAck(tag, false);
        } catch (Exception ex) {
            log.error("Handle pay success failed, payload={}", payload, ex);
            channel.basicNack(tag, false, false);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void handleTimeout(Map<String, Object> payload, Message message, Channel channel) throws IOException {
        long tag = message.getMessageProperties().getDeliveryTag();
        try {
            String orderNo = String.valueOf(payload.get("orderNo"));
            OrderRecord order = orderMapper.selectOne(Wrappers.<OrderRecord>lambdaQuery()
                    .eq(OrderRecord::getOrderNo, orderNo)
                    .last("limit 1"));
            if (order != null) {
                int updated = orderMapper.update(null, Wrappers.<OrderRecord>lambdaUpdate()
                        .eq(OrderRecord::getOrderNo, orderNo)
                        .eq(OrderRecord::getStatus, OrderStatus.UNPAID.name())
                        .set(OrderRecord::getStatus, OrderStatus.CLOSED.name())
                        .set(OrderRecord::getUpdatedAt, LocalDateTime.now()));
                if (updated > 0) {
                    tradeClient.release(order.getItemId());
                }
            }
            channel.basicAck(tag, false);
        } catch (Exception ex) {
            log.error("Handle order timeout failed, payload={}", payload, ex);
            channel.basicNack(tag, false, false);
        }
    }

    private OrderRecord require(Long id) {
        OrderRecord order = orderMapper.selectById(id);
        if (order == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "订单不存在");
        }
        return order;
    }

    private TradeItemVO unwrap(Result<TradeItemVO> result) {
        if (result.getCode() != 0 || result.getData() == null) {
            throw new BusinessException(ErrorCode.CONFLICT, result.getMessage());
        }
        return result.getData();
    }
}
