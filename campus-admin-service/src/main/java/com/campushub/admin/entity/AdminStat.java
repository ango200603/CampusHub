package com.campushub.admin.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Admin statistic snapshot entity.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("admin_stats")
public class AdminStat {
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;
    private String name;
    private Object value;
}
