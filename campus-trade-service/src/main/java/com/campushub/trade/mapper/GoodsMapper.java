package com.campushub.trade.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.campushub.trade.entity.Goods;
import org.apache.ibatis.annotations.Mapper;

/**
 * Mapper for goods.
 */
@Mapper
public interface GoodsMapper extends BaseMapper<Goods> {
}
