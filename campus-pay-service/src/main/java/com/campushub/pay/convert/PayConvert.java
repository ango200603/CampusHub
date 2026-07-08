package com.campushub.pay.convert;

import com.campushub.pay.vo.PayRecordVO;
import com.campushub.pay.vo.PayVO;

/**
 * Static payment converters.
 */
public final class PayConvert {
    private PayConvert() {
    }

    /**
     * Converts payment record VO to payment VO.
     *
     * @param record payment record
     * @return payment VO
     */
    public static PayVO toPayVO(PayRecordVO record) {
        return PayVO.from(record);
    }
}
