package com.campushub.trade.controller;

import com.campushub.common.api.Result;
import com.campushub.trade.service.TradeItemService;
import com.campushub.trade.vo.TradeItemVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/trades/internal/items")
@RequiredArgsConstructor
public class TradeInternalController {
    private final TradeItemService tradeItemService;

    @GetMapping("/{id}")
    public Result<TradeItemVO> detail(@PathVariable Long id) {
        return Result.ok(tradeItemService.detail(id));
    }

    @PostMapping("/{id}/lock-for-order")
    public Result<TradeItemVO> lockForOrder(@PathVariable Long id) {
        return Result.ok(tradeItemService.lockForOrder(id));
    }

    @PostMapping("/{id}/release")
    public Result<Void> release(@PathVariable Long id) {
        tradeItemService.release(id);
        return Result.ok();
    }

    @PostMapping("/{id}/sold")
    public Result<Void> sold(@PathVariable Long id) {
        tradeItemService.markSold(id);
        return Result.ok();
    }
}
