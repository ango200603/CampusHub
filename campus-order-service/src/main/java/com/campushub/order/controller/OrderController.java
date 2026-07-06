package com.campushub.order.controller;

import com.campushub.common.api.Result;
import com.campushub.order.dto.CreateOrderRequest;
import com.campushub.order.service.OrderService;
import com.campushub.order.vo.OrderVO;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @PostMapping
    public Result<OrderVO> create(@RequestHeader("X-User-Id") Long userId,
                                  @Valid @RequestBody CreateOrderRequest request) {
        return Result.ok(orderService.create(userId, request));
    }

    @GetMapping("/{id}")
    public Result<OrderVO> get(@RequestHeader("X-User-Id") Long userId, @PathVariable Long id) {
        return Result.ok(orderService.get(userId, id));
    }

    @GetMapping("/my")
    public Result<List<OrderVO>> my(@RequestHeader("X-User-Id") Long userId) {
        return Result.ok(orderService.my(userId));
    }

    @PostMapping("/{id}/cancel")
    public Result<Void> cancel(@RequestHeader("X-User-Id") Long userId, @PathVariable Long id) {
        orderService.cancel(userId, id);
        return Result.ok();
    }
}
