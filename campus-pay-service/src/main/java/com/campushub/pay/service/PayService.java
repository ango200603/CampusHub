package com.campushub.pay.service;

import com.campushub.pay.dto.PayCreateRequest;
import com.campushub.pay.dto.PayMockSuccessRequest;
import com.campushub.pay.vo.PayRecordVO;

public interface PayService {
    PayRecordVO create(PayCreateRequest request);

    PayRecordVO mockSuccess(PayMockSuccessRequest request);

    PayRecordVO getByOrderNo(String orderNo);
}
