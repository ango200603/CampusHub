package com.campushub.pay.client;

import com.campushub.common.api.Result;
import java.util.Map;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * Feign client for order-service.
 */
@FeignClient(name = "campus-order-service", path = "/orders")
@SuppressWarnings("unused")
public interface OrderClient {
    /**
     * Gets an order by id.
     *
     * @param id order id
     * @return order result
     */
    @GetMapping("/{id}")
    Result<Map<String, Object>> get(@PathVariable("id") Long id);
}
