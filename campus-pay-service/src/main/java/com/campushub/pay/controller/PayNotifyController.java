package com.campushub.pay.controller;

import com.campushub.common.api.Result;
import com.campushub.pay.convert.PayConvert;
import com.campushub.pay.dto.PayMockSuccessRequest;
import com.campushub.pay.service.PayService;
import com.campushub.pay.vo.PayVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Mock payment notification endpoints.
 */
@RestController
@RequestMapping("/pay/notify")
@RequiredArgsConstructor
public class PayNotifyController {
    private final PayService payService;

    /**
     * Handles a mock success notification.
     *
     * @param request mock success request
     * @return payment record
     */
    @PostMapping("/mock-success")
    public Result<PayVO> mockSuccess(@RequestBody PayMockSuccessRequest request) {
        return Result.ok(PayConvert.toPayVO(payService.mockSuccess(request)));
    }
}
