package com.campushub.trade.convert;

import com.campushub.trade.vo.GoodsVO;
import com.campushub.trade.vo.TradeItemVO;

/**
 * Static goods converters.
 */
public final class GoodsConvert {
    private GoodsConvert() {
    }

    /**
     * Converts trade item VO to goods VO.
     *
     * @param item trade item VO
     * @return goods VO
     */
    public static GoodsVO toGoodsVO(TradeItemVO item) {
        return GoodsVO.from(item);
    }
}
