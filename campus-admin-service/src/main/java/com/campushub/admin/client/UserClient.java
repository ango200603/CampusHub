package com.campushub.admin.client;

import com.campushub.common.api.Result;
import java.util.Map;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * Feign client for user-service.
 */
@FeignClient(name = "campus-user-service", path = "/users")
@SuppressWarnings("unused")
public interface UserClient {
    /**
     * Gets user detail.
     *
     * @param id user id
     * @return user result
     */
    @GetMapping("/{id}")
    Result<Map<String, Object>> get(@PathVariable("id") Long id);
}
