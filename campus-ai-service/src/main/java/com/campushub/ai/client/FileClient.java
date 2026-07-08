package com.campushub.ai.client;

import com.campushub.common.constant.CommonConstant;
import com.campushub.common.api.Result;
import java.util.Map;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

/**
 * Feign client for file-service.
 */
@FeignClient(name = "campus-file-service", path = "/files")
@SuppressWarnings("unused")
public interface FileClient {
    /**
     * Gets a file record through file-service.
     *
     * @param userId user id
     * @param id file id
     * @return file record result
     */
    @GetMapping("/{id}")
    Result<Map<String, Object>> get(@RequestHeader(CommonConstant.HEADER_USER_ID) Long userId, @PathVariable("id") Long id);
}
