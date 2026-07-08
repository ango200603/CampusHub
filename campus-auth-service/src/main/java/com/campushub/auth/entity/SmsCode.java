package com.campushub.auth.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * SMS verification code record for optional persistence.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("sms_codes")
public class SmsCode {
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;
    private String phone;
    private String code;
    private String scene;
    private LocalDateTime expireAt;
    private LocalDateTime createdAt;
}
