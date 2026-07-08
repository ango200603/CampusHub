package com.campushub.trade.service;

import com.campushub.trade.dto.CreateTradeItemRequest;
import com.campushub.trade.dto.UpdateTradeItemRequest;
import com.campushub.trade.vo.TradeItemVO;
import java.util.List;

/**
 * Trade service contract.
 */
public interface TradeItemService {
    /**
     * Creates a new record.
     */
    TradeItemVO create(Long sellerId, CreateTradeItemRequest request);

    /**
     * Returns a filtered record list.
     */
    List<TradeItemVO> list(String category, String keyword);

    /**
     * Returns record details.
     */
    TradeItemVO detail(Long id);

    /**
     * Updates an existing record.
     */
    TradeItemVO update(Long sellerId, Long id, UpdateTradeItemRequest request);

    /**
     * Takes a published item off shelf.
     */
    void offShelf(Long sellerId, Long id);

    /**
     * Locks an item for order creation.
     */
    TradeItemVO lockForOrder(Long itemId);

    /**
     * Releases a locked item.
     */
    void release(Long itemId);

    /**
     * Marks an item as sold.
     */
    void markSold(Long itemId);
}
