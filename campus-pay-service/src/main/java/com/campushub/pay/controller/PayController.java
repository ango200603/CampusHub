package com.campushub.pay.controller;

import com.campushub.common.api.Result;
import com.campushub.pay.convert.PayConvert;
import com.campushub.pay.dto.PayCreateDTO;
import com.campushub.pay.dto.PayMockSuccessRequest;
import com.campushub.pay.service.PayService;
import com.campushub.pay.vo.PayVO;
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
@SuppressWarnings("unused")
public class PayController {
    private final PayService payService;

    /**
     * Creates a new record.
     */
    @PostMapping("/create")
    public Result<PayVO> create(@Valid @RequestBody PayCreateDTO request) {
        return Result.ok(PayConvert.toPayVO(payService.create(request.toPayCreateRequest())));
    }

    /**
     * Completes a mock success callback.
     */
    @PostMapping("/mock-success")
    public Result<PayVO> mockSuccess(@RequestBody PayMockSuccessRequest request) {
        return Result.ok(PayConvert.toPayVO(payService.mockSuccess(request)));
    }

    /**
     * Returns a record by id.
     */
    @GetMapping("/records/{orderNo}")
    public Result<PayVO> get(@PathVariable String orderNo) {
        return Result.ok(PayConvert.toPayVO(payService.getByOrderNo(orderNo)));
    }
}
