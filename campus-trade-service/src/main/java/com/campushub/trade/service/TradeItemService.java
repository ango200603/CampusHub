package com.campushub.trade.service;

import com.campushub.trade.dto.CreateTradeItemRequest;
import com.campushub.trade.dto.UpdateTradeItemRequest;
import com.campushub.trade.vo.TradeItemVO;
import java.util.List;

public interface TradeItemService {
    TradeItemVO create(Long sellerId, CreateTradeItemRequest request);

    List<TradeItemVO> list(String category, String keyword);

    TradeItemVO detail(Long id);

    TradeItemVO update(Long sellerId, Long id, UpdateTradeItemRequest request);

    void offShelf(Long sellerId, Long id);

    TradeItemVO lockForOrder(Long itemId);

    void release(Long itemId);

    void markSold(Long itemId);
}
