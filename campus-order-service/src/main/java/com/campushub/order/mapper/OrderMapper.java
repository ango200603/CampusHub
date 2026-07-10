package com.campushub.order.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.campushub.order.entity.Order;
import org.apache.ibatis.annotations.Mapper;

/**
 * Mapper for orders.
 */
@Mapper
public interface OrderMapper extends BaseMapper<Order> {
}
