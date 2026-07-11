package com.campushub.trade.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.campushub.common.api.Result;
import com.campushub.common.constant.RedisKeyConstant;
import com.campushub.common.exception.BusinessException;
import com.campushub.common.exception.ErrorCode;
import com.campushub.trade.client.UserClient;
import com.campushub.trade.client.dto.UserBatchRequest;
import com.campushub.trade.client.vo.UserSummaryVO;
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
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * Trade service implementation.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class TradeItemServiceImpl implements TradeItemService {
    private static final String UNKNOWN_SELLER = "未知用户";

    private final TradeItemMapper tradeItemMapper;
    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;
    private final UserClient userClient;

    /**
     * {@inheritDoc}
     */
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
        redisTemplate.delete(RedisKeyConstant.TRADE_HOT_LIST);
        TradeItemVO vo = TradeItemVO.from(item);
        enrichSellers(List.of(vo));
        return vo;
    }

    /**
     * {@inheritDoc}
     */
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
        List<TradeItemVO> items = tradeItemMapper.selectList(query).stream().map(TradeItemVO::from).toList();
        enrichSellers(items);
        return items;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TradeItemVO detail(Long id) {
        String key = RedisKeyConstant.tradeItem(id);
        String cached = redisTemplate.opsForValue().get(key);
        if (cached != null) {
            try {
                TradeItemVO cachedItem = objectMapper.readValue(cached, TradeItemVO.class);
                if (!StringUtils.hasText(cachedItem.getSellerNickname())) {
                    enrichSellers(List.of(cachedItem));
                    cacheDetail(key, cachedItem);
                }
                return cachedItem;
            } catch (Exception ignored) {
                redisTemplate.delete(key);
            }
        }
        TradeItem item = require(id);
        TradeItemVO vo = TradeItemVO.from(item);
        enrichSellers(List.of(vo));
        cacheDetail(key, vo);
        return vo;
    }

    /**
     * {@inheritDoc}
     */
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
        TradeItemVO vo = TradeItemVO.from(item);
        enrichSellers(List.of(vo));
        return vo;
    }

    /**
     * {@inheritDoc}
     */
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

    /**
     * {@inheritDoc}
     */
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

    /**
     * {@inheritDoc}
     */
    @Override
    public void release(Long itemId) {
        tradeItemMapper.update(null, Wrappers.<TradeItem>lambdaUpdate()
                .eq(TradeItem::getId, itemId)
                .eq(TradeItem::getStatus, TradeItemStatus.LOCKED.name())
                .set(TradeItem::getStatus, TradeItemStatus.AVAILABLE.name())
                .set(TradeItem::getUpdatedAt, LocalDateTime.now()));
        evict(itemId);
    }

    /**
     * {@inheritDoc}
     */
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
        redisTemplate.delete(RedisKeyConstant.tradeItem(id));
        redisTemplate.delete(RedisKeyConstant.TRADE_HOT_LIST);
    }

    private void enrichSellers(List<TradeItemVO> items) {
        if (items == null || items.isEmpty()) {
            return;
        }
        List<String> sellerIds = items.stream()
                .map(TradeItemVO::getSellerId)
                .filter(StringUtils::hasText)
                .distinct()
                .toList();
        Map<String, UserSummaryVO> sellers = loadSellers(sellerIds);
        items.forEach(item -> applySeller(item, sellers.get(item.getSellerId())));
    }

    private Map<String, UserSummaryVO> loadSellers(List<String> sellerIds) {
        if (sellerIds.isEmpty()) {
            return Map.of();
        }
        try {
            Result<List<UserSummaryVO>> result = userClient.batch(
                    UserBatchRequest.builder().userIds(sellerIds).build());
            if (result == null || result.getCode() != 0 || result.getData() == null) {
                return Map.of();
            }
            return result.getData().stream()
                    .filter(user -> StringUtils.hasText(user.getId()))
                    .collect(Collectors.toMap(UserSummaryVO::getId, Function.identity(), (left, right) -> left));
        } catch (RuntimeException ex) {
            log.warn("Unable to load seller summaries: {}", ex.getMessage());
            return Map.of();
        }
    }

    private void applySeller(TradeItemVO item, UserSummaryVO seller) {
        item.setSellerNickname(seller != null && StringUtils.hasText(seller.getNickname())
                ? seller.getNickname() : UNKNOWN_SELLER);
        item.setSellerAvatarUrl(seller != null && seller.getAvatarUrl() != null ? seller.getAvatarUrl() : "");
    }

    private void cacheDetail(String key, TradeItemVO item) {
        try {
            redisTemplate.opsForValue().set(key, objectMapper.writeValueAsString(item), Duration.ofMinutes(10));
        } catch (Exception ignored) {
            // A cache write failure must not make the product detail unavailable.
        }
    }
}
