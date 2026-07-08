package com.campushub.order.client;

import com.campushub.common.api.Result;
import java.util.Map;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * Feign client for notice-service.
 */
@FeignClient(name = "campus-notice-service", path = "/notices")
@SuppressWarnings("unused")
public interface NoticeClient {
    /**
     * Creates a notice through notice-service.
     *
     * @param request notice request map
     * @return empty result
     */
    @PostMapping
    Result<Map<String, Object>> create(@RequestBody Map<String, Object> request);
}
