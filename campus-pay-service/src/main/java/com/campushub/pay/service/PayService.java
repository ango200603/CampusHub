package com.campushub.pay.service;

import com.campushub.pay.dto.PayCreateRequest;
import com.campushub.pay.dto.PayMockSuccessRequest;
import com.campushub.pay.vo.PayRecordVO;

/**
 * Pay service contract.
 */
public interface PayService {
    /**
     * Creates a new record.
     */
    PayRecordVO create(PayCreateRequest request);

    /**
     * Completes a mock success callback.
     */
    PayRecordVO mockSuccess(PayMockSuccessRequest request);

    /**
     * Returns a payment record by order number.
     */
    PayRecordVO getByOrderNo(String orderNo);
}
