package com.campushub.pay.vo;

import com.campushub.pay.entity.PayRecord;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PayRecordVO {
    private String orderNo;
    private String payNo;
    private BigDecimal amount;
    private String status;
    private LocalDateTime createdAt;

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
