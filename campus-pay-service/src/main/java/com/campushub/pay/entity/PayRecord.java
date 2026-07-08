package com.campushub.pay.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Payment record entity.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("pay_records")
public class PayRecord {
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;
    private String orderNo;
    private String payNo;
    private BigDecimal amount;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
