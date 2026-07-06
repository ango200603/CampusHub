package com.campushub.order.client;

import com.campushub.common.api.Result;
import com.campushub.order.vo.TradeItemVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(name = "campus-trade-service", path = "/trades/internal/items")
public interface TradeClient {
    @GetMapping("/{id}")
    Result<TradeItemVO> detail(@PathVariable("id") Long id);

    @PostMapping("/{id}/lock-for-order")
    Result<TradeItemVO> lockForOrder(@PathVariable("id") Long id);

    @PostMapping("/{id}/release")
    Result<Void> release(@PathVariable("id") Long id);

    @PostMapping("/{id}/sold")
    Result<Void> sold(@PathVariable("id") Long id);
}
