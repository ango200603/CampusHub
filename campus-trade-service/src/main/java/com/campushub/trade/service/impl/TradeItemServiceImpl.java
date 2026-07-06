package com.campushub.trade.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.campushub.common.exception.BusinessException;
import com.campushub.common.exception.ErrorCode;
import com.campushub.trade.dto.CreateTradeItemRequest;
import com.campushub.trade.dto.UpdateTradeItemRequest;
import com.campushub.trade.entity.TradeItem;
import com.campushub.trade.enums.TradeItemStatus;
import com.campushub.trade.mapper.TradeItemMapper;
import com.campushub.trade.service.TradeItemService;
import com.campushub.trade.vo.TradeItemVO;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class TradeItemServiceImpl implements TradeItemService {
    private final TradeItemMapper tradeItemMapper;
    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    @Override
    public TradeItemVO create(Long sellerId, CreateTradeItemRequest request) {
        LocalDateTime now = LocalDateTime.now();
        TradeItem item = new TradeItem();
        item.setSellerId(sellerId);
        item.setTitle(request.getTitle());
        item.setDescription(request.getDescription());
        item.setPrice(request.getPrice());
        item.setCategory(request.getCategory());
        item.setCoverUrl(request.getCoverUrl());
        item.setStatus(TradeItemStatus.AVAILABLE.name());
        item.setCreatedAt(now);
        item.setUpdatedAt(now);
        tradeItemMapper.insert(item);
        redisTemplate.delete("trade:hot:list");
        return TradeItemVO.from(item);
    }

    @Override
    public List<TradeItemVO> list(String category, String keyword) {
        LambdaQueryWrapper<TradeItem> query = Wrappers.<TradeItem>lambdaQuery()
                .eq(TradeItem::getStatus, TradeItemStatus.AVAILABLE.name())
                .orderByDesc(TradeItem::getCreatedAt);
        if (StringUtils.hasText(category)) {
            query.eq(TradeItem::getCategory, category);
        }
        if (StringUtils.hasText(keyword)) {
            query.like(TradeItem::getTitle, keyword);
        }
        return tradeItemMapper.selectList(query).stream().map(TradeItemVO::from).toList();
    }

    @Override
    public TradeItemVO detail(Long id) {
        String key = "trade:item:" + id;
        String cached = redisTemplate.opsForValue().get(key);
        if (cached != null) {
            try {
                return objectMapper.readValue(cached, TradeItemVO.class);
            } catch (Exception ignored) {
                redisTemplate.delete(key);
            }
        }
        TradeItem item = require(id);
        TradeItemVO vo = TradeItemVO.from(item);
        try {
            redisTemplate.opsForValue().set(key, objectMapper.writeValueAsString(vo), Duration.ofMinutes(10));
        } catch (Exception ignored) {
        }
        return vo;
    }

    @Override
    public TradeItemVO update(Long sellerId, Long id, UpdateTradeItemRequest request) {
        TradeItem item = require(id);
        if (!item.getSellerId().equals(sellerId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "只能修改自己发布的商品");
        }
        if (StringUtils.hasText(request.getTitle())) {
            item.setTitle(request.getTitle());
        }
        if (request.getDescription() != null) {
            item.setDescription(request.getDescription());
        }
        if (request.getPrice() != null) {
            item.setPrice(request.getPrice());
        }
        if (StringUtils.hasText(request.getCategory())) {
            item.setCategory(request.getCategory());
        }
        if (request.getCoverUrl() != null) {
            item.setCoverUrl(request.getCoverUrl());
        }
        item.setUpdatedAt(LocalDateTime.now());
        tradeItemMapper.updateById(item);
        evict(id);
        return TradeItemVO.from(item);
    }

    @Override
    public void offShelf(Long sellerId, Long id) {
        TradeItem item = require(id);
        if (!item.getSellerId().equals(sellerId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "只能下架自己发布的商品");
        }
        item.setStatus(TradeItemStatus.OFF_SHELF.name());
        item.setUpdatedAt(LocalDateTime.now());
        tradeItemMapper.updateById(item);
        evict(id);
    }

    @Override
    public TradeItemVO lockForOrder(Long itemId) {
        TradeItem item = require(itemId);
        if (!TradeItemStatus.AVAILABLE.name().equals(item.getStatus())) {
            throw new BusinessException(ErrorCode.CONFLICT, "商品不可购买");
        }
        int updated = tradeItemMapper.update(null, Wrappers.<TradeItem>lambdaUpdate()
                .eq(TradeItem::getId, itemId)
                .eq(TradeItem::getStatus, TradeItemStatus.AVAILABLE.name())
                .set(TradeItem::getStatus, TradeItemStatus.LOCKED.name())
                .set(TradeItem::getUpdatedAt, LocalDateTime.now()));
        if (updated == 0) {
            throw new BusinessException(ErrorCode.CONFLICT, "商品已被锁定或售出");
        }
        evict(itemId);
        return TradeItemVO.from(require(itemId));
    }

    @Override
    public void release(Long itemId) {
        tradeItemMapper.update(null, Wrappers.<TradeItem>lambdaUpdate()
                .eq(TradeItem::getId, itemId)
                .eq(TradeItem::getStatus, TradeItemStatus.LOCKED.name())
                .set(TradeItem::getStatus, TradeItemStatus.AVAILABLE.name())
                .set(TradeItem::getUpdatedAt, LocalDateTime.now()));
        evict(itemId);
    }

    @Override
    public void markSold(Long itemId) {
        int updated = tradeItemMapper.update(null, Wrappers.<TradeItem>lambdaUpdate()
                .eq(TradeItem::getId, itemId)
                .in(TradeItem::getStatus, TradeItemStatus.LOCKED.name(), TradeItemStatus.AVAILABLE.name())
                .set(TradeItem::getStatus, TradeItemStatus.SOLD.name())
                .set(TradeItem::getUpdatedAt, LocalDateTime.now()));
        if (updated == 0) {
            TradeItem item = require(itemId);
            if (!TradeItemStatus.SOLD.name().equals(item.getStatus())) {
                throw new BusinessException(ErrorCode.CONFLICT, "商品状态不可标记售出");
            }
        }
        evict(itemId);
    }

    private TradeItem require(Long id) {
        TradeItem item = tradeItemMapper.selectById(id);
        if (item == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "商品不存在");
        }
        return item;
    }

    private void evict(Long id) {
        redisTemplate.delete("trade:item:" + id);
        redisTemplate.delete("trade:hot:list");
    }
}
