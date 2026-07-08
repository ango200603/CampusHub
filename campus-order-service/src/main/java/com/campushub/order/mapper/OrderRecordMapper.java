package com.campushub.order.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.campushub.order.entity.OrderRecord;
import org.apache.ibatis.annotations.Mapper;

/**
 * Mapper for order records.
 */
@Mapper
public interface OrderRecordMapper extends BaseMapper<OrderRecord> {
}
