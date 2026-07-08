package com.campushub.pay.vo;

import com.campushub.pay.entity.PayRecord;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Payment record response.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PayRecordVO {
    private String orderNo;
    private String payNo;
    private BigDecimal amount;
    private String status;
    private LocalDateTime createdAt;

    /**
     * Converts a payment record entity to VO.
     *
     * @param record payment record entity
     * @return payment record VO
     */
    public static PayRecordVO from(PayRecord record) {
        return PayRecordVO.builder()
                .orderNo(record.getOrderNo())
                .payNo(record.getPayNo())
                .amount(record.getAmount())
                .status(record.getStatus())
                .createdAt(record.getCreatedAt())
                .build();
    }
}
