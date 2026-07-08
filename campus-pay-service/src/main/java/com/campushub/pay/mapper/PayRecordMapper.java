package com.campushub.pay.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.campushub.pay.entity.PayRecord;
import org.apache.ibatis.annotations.Mapper;

/**
 * Mapper for payment records.
 */
@Mapper
public interface PayRecordMapper extends BaseMapper<PayRecord> {
}
