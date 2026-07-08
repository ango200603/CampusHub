package com.campushub.auth.client;

import com.campushub.auth.dto.LoginUserRequest;
import com.campushub.auth.vo.UserVO;
import com.campushub.common.api.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * Auth Feign client.
 */
@FeignClient(name = "campus-user-service", path = "/users/internal")
public interface UserClient {
    /**
     * Returns an existing user or creates one.
     */
    @PostMapping("/login-user")
    Result<UserVO> getOrCreate(@RequestBody LoginUserRequest request);
}
