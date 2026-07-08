package com.campushub.trade.controller;

import com.campushub.common.constant.CommonConstant;
import com.campushub.common.api.Result;
import com.campushub.trade.dto.CreateTradeItemRequest;
import com.campushub.trade.dto.UpdateTradeItemRequest;
import com.campushub.trade.service.TradeItemService;
import com.campushub.trade.vo.TradeItemVO;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Trade API controller.
 */
@RestController
@RequestMapping("/trades/items")
@RequiredArgsConstructor
public class TradeItemController {
    private final TradeItemService tradeItemService;

    /**
     * Creates a new record.
     */
    @PostMapping
    public Result<TradeItemVO> create(@RequestHeader(CommonConstant.HEADER_USER_ID) Long userId,
                                      @Valid @RequestBody CreateTradeItemRequest request) {
        return Result.ok(tradeItemService.create(userId, request));
    }

    /**
     * Returns a filtered record list.
     */
    @GetMapping
    public Result<List<TradeItemVO>> list(@RequestParam(required = false) String category,
                                          @RequestParam(required = false) String keyword) {
        return Result.ok(tradeItemService.list(category, keyword));
    }

    /**
     * Returns record details.
     */
    @GetMapping("/{id}")
    public Result<TradeItemVO> detail(@PathVariable Long id) {
        return Result.ok(tradeItemService.detail(id));
    }

    /**
     * Updates an existing record.
     */
    @PutMapping("/{id}")
    public Result<TradeItemVO> update(@RequestHeader(CommonConstant.HEADER_USER_ID) Long userId,
                                      @PathVariable Long id,
                                      @Valid @RequestBody UpdateTradeItemRequest request) {
        return Result.ok(tradeItemService.update(userId, id, request));
    }

    /**
     * Deletes an existing record.
     */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@RequestHeader(CommonConstant.HEADER_USER_ID) Long userId, @PathVariable Long id) {
        tradeItemService.offShelf(userId, id);
        return Result.ok();
    }
}
