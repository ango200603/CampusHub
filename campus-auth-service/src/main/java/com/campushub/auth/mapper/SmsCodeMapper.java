package com.campushub.auth.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.campushub.auth.entity.SmsCode;
import org.apache.ibatis.annotations.Mapper;

/**
 * Mapper for SMS code records.
 */
@Mapper
public interface SmsCodeMapper extends BaseMapper<SmsCode> {
}
