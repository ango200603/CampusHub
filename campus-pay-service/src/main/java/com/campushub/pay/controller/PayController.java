package com.campushub.pay.controller;

import com.campushub.common.api.Result;
import com.campushub.pay.dto.PayCreateRequest;
import com.campushub.pay.dto.PayMockSuccessRequest;
import com.campushub.pay.service.PayService;
import com.campushub.pay.vo.PayRecordVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Pay API controller.
 */
@RestController
@RequestMapping("/pay")
@RequiredArgsConstructor
public class PayController {
    private final PayService payService;

    /**
     * Creates a new record.
     */
    @PostMapping("/create")
    public Result<PayRecordVO> create(@Valid @RequestBody PayCreateRequest request) {
        return Result.ok(payService.create(request));
    }

    /**
     * Completes a mock success callback.
     */
    @PostMapping("/mock-success")
    public Result<PayRecordVO> mockSuccess(@RequestBody PayMockSuccessRequest request) {
        return Result.ok(payService.mockSuccess(request));
    }

    /**
     * Returns a record by id.
     */
    @GetMapping("/records/{orderNo}")
    public Result<PayRecordVO> get(@PathVariable String orderNo) {
        return Result.ok(payService.getByOrderNo(orderNo));
    }
}
