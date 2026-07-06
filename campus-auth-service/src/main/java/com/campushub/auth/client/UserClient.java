package com.campushub.auth.client;

import com.campushub.auth.dto.LoginUserRequest;
import com.campushub.auth.vo.UserVO;
import com.campushub.common.api.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "campus-user-service", path = "/users/internal")
public interface UserClient {
    @PostMapping("/login-user")
    Result<UserVO> getOrCreate(@RequestBody LoginUserRequest request);
}
