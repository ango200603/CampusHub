package com.campushub.order.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.campushub.order.entity.Order;
import org.apache.ibatis.annotations.Mapper;

/**
 * Mapper for orders.
 */
@Mapper
@SuppressWarnings("unused")
public interface OrderMapper extends BaseMapper<Order> {
}
