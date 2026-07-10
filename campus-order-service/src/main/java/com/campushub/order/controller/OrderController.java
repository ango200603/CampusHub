package com.campushub.order.controller;

import com.campushub.common.constant.CommonConstant;
import com.campushub.common.api.Result;
import com.campushub.order.convert.OrderConvert;
import com.campushub.order.dto.OrderCreateDTO;
import com.campushub.order.dto.OrderQueryDTO;
import com.campushub.order.service.OrderService;
import com.campushub.order.vo.OrderVO;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Order API controller.
 */
@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    /**
     * Creates a new record.
     */
    @PostMapping
    public Result<OrderVO> create(@RequestHeader(CommonConstant.HEADER_USER_ID) Long userId,
                                  @Valid @RequestBody OrderCreateDTO request) {
        return Result.ok(orderService.create(userId, OrderConvert.toCreateOrderRequest(request)));
    }

    /**
     * Returns a record by id.
     */
    @GetMapping("/{id}")
    public Result<OrderVO> get(@RequestHeader(CommonConstant.HEADER_USER_ID) Long userId, @PathVariable Long id) {
        return Result.ok(orderService.get(userId, id));
    }

    /**
     * Queries records owned by the current user.
     */
    @GetMapping
    public Result<List<OrderVO>> list(@RequestHeader(CommonConstant.HEADER_USER_ID) Long userId,
                                      @ModelAttribute OrderQueryDTO query) {
        query.setUserId(userId);
        return Result.ok(orderService.query(query));
    }

    /**
     * Returns records owned by the current user.
     */
    @GetMapping("/my")
    public Result<List<OrderVO>> my(@RequestHeader(CommonConstant.HEADER_USER_ID) Long userId) {
        return Result.ok(orderService.my(userId));
    }

    /**
     * Cancels a pending record.
     */
    @PostMapping("/{id}/cancel")
    public Result<Void> cancel(@RequestHeader(CommonConstant.HEADER_USER_ID) Long userId, @PathVariable Long id) {
        orderService.cancel(userId, id);
        return Result.ok();
    }
}
