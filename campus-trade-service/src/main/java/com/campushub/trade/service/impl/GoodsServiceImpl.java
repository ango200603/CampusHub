package com.campushub.trade.service.impl;

import com.campushub.trade.convert.GoodsConvert;
import com.campushub.trade.dto.GoodsPublishDTO;
import com.campushub.trade.dto.GoodsQueryDTO;
import com.campushub.trade.service.GoodsService;
import com.campushub.trade.service.TradeItemService;
import com.campushub.trade.vo.GoodsVO;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Goods service facade implementation.
 */
@Service
@RequiredArgsConstructor
public class GoodsServiceImpl implements GoodsService {
    private final TradeItemService tradeItemService;

    /**
     * Publishes goods.
     *
     * @param sellerId seller id
     * @param request publish request
     * @return goods VO
     */
    @Override
    public GoodsVO publish(Long sellerId, GoodsPublishDTO request) {
        return GoodsConvert.toGoodsVO(tradeItemService.create(sellerId, request.toCreateTradeItemRequest()));
    }

    /**
     * Lists goods.
     *
     * @param query query parameters
     * @return goods list
     */
    @Override
    public List<GoodsVO> list(GoodsQueryDTO query) {
        return tradeItemService.list(query.getCategory(), query.getKeyword()).stream().map(GoodsConvert::toGoodsVO).toList();
    }

    /**
     * Gets goods detail.
     *
     * @param id goods id
     * @return goods detail
     */
    @Override
    public GoodsVO detail(Long id) {
        return GoodsConvert.toGoodsVO(tradeItemService.detail(id));
    }

    /**
     * Takes goods off shelf.
     *
     * @param sellerId seller id
     * @param id goods id
     */
    @Override
    public void offShelf(Long sellerId, Long id) {
        tradeItemService.offShelf(sellerId, id);
    }
}
