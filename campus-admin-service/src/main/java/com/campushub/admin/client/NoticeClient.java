package com.campushub.admin.client;

import com.campushub.common.api.Result;
import java.util.Map;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * Feign client for publishing notices.
 */
@FeignClient(name = "campus-notice-service", path = "/notices")
public interface NoticeClient {
    /**
     * Publishes a notice.
     *
     * @param request notice request
     * @return notice result
     */
    @PostMapping
    Result<Map<String, Object>> create(@RequestBody Map<String, Object> request);
}
