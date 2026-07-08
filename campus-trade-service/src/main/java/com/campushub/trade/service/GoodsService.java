package com.campushub.trade.service;

import com.campushub.trade.dto.GoodsPublishDTO;
import com.campushub.trade.dto.GoodsQueryDTO;
import com.campushub.trade.vo.GoodsVO;
import java.util.List;

/**
 * Goods service facade.
 */
public interface GoodsService {
    /**
     * Publishes goods.
     *
     * @param sellerId seller id
     * @param request publish request
     * @return goods VO
     */
    GoodsVO publish(Long sellerId, GoodsPublishDTO request);

    /**
     * Lists goods.
     *
     * @param query query parameters
     * @return goods list
     */
    List<GoodsVO> list(GoodsQueryDTO query);

    /**
     * Gets goods detail.
     *
     * @param id goods id
     * @return goods detail
     */
    GoodsVO detail(Long id);

    /**
     * Takes goods off shelf.
     *
     * @param sellerId seller id
     * @param id goods id
     */
    void offShelf(Long sellerId, Long id);
}
