package com.campushub.trade.controller;

import com.campushub.common.constant.CommonConstant;
import com.campushub.common.api.Result;
import com.campushub.trade.dto.GoodsPublishDTO;
import com.campushub.trade.dto.GoodsQueryDTO;
import com.campushub.trade.service.GoodsService;
import com.campushub.trade.vo.GoodsVO;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Goods endpoints.
 */
@RestController
@RequestMapping("/goods")
@RequiredArgsConstructor
public class GoodsController {
    private final GoodsService goodsService;

    /**
     * Publishes goods.
     *
     * @param userId seller id from gateway
     * @param request publish request
     * @return goods VO
     */
    @PostMapping
    public Result<GoodsVO> publish(@RequestHeader(CommonConstant.HEADER_USER_ID) Long userId, @Valid @RequestBody GoodsPublishDTO request) {
        return Result.ok(goodsService.publish(userId, request));
    }

    /**
     * Lists goods.
     *
     * @param query query parameters
     * @return goods list
     */
    @GetMapping
    public Result<List<GoodsVO>> list(GoodsQueryDTO query) {
        return Result.ok(goodsService.list(query));
    }

    /**
     * Gets goods detail.
     *
     * @param id goods id
     * @return goods VO
     */
    @GetMapping("/{id}")
    public Result<GoodsVO> detail(@PathVariable Long id) {
        return Result.ok(goodsService.detail(id));
    }

    /**
     * Takes goods off shelf.
     *
     * @param userId seller id from gateway
     * @param id goods id
     * @return empty result
     */
    @DeleteMapping("/{id}")
    public Result<Void> offShelf(@RequestHeader(CommonConstant.HEADER_USER_ID) Long userId, @PathVariable Long id) {
        goodsService.offShelf(userId, id);
        return Result.ok();
    }
}
