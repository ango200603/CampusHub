package com.campushub.pay.vo;

import com.campushub.pay.enums.PayStatusEnum;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Payment response object.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PayVO {
    private String orderNo;
    private String payNo;
    private BigDecimal amount;
    private PayStatusEnum status;
    private LocalDateTime createdAt;

    /**
     * Converts a payment record VO.
     *
     * @param record payment record
     * @return payment VO
     */
    public static PayVO from(PayRecordVO record) {
        return PayVO.builder()
                .orderNo(record.getOrderNo())
                .payNo(record.getPayNo())
                .amount(record.getAmount())
                .status(PayStatusEnum.valueOf(record.getStatus()))
                .createdAt(record.getCreatedAt())
                .build();
    }
}
