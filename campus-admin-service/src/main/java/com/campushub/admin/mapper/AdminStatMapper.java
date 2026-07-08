package com.campushub.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.campushub.admin.entity.AdminStat;
import org.apache.ibatis.annotations.Mapper;

/**
 * Mapper for admin stat snapshots.
 */
@Mapper
@SuppressWarnings("unused")
public interface AdminStatMapper extends BaseMapper<AdminStat> {
}
