package com.campushub.user.controller;

import com.campushub.common.api.Result;
import com.campushub.user.dto.LoginUserRequest;
import com.campushub.user.dto.PointsChangeRequest;
import com.campushub.user.dto.UpdateProfileRequest;
import com.campushub.user.service.UserService;
import com.campushub.user.vo.UserVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/me")
    public Result<UserVO> me(@RequestHeader("X-User-Id") Long userId) {
        return Result.ok(userService.getById(userId));
    }

    @PutMapping("/me")
    public Result<UserVO> updateMe(@RequestHeader("X-User-Id") Long userId,
                                   @Valid @RequestBody UpdateProfileRequest request) {
        return Result.ok(userService.updateMe(userId, request));
    }

    @GetMapping("/{id}")
    public Result<UserVO> get(@PathVariable Long id) {
        return Result.ok(userService.getById(id));
    }

    @GetMapping("/points/me")
    public Result<Integer> points(@RequestHeader("X-User-Id") Long userId) {
        return Result.ok(userService.getPoints(userId));
    }

    @PostMapping("/points/increase")
    public Result<Void> increase(@Valid @RequestBody PointsChangeRequest request) {
        userService.increasePoints(request);
        return Result.ok();
    }

    @PostMapping("/points/decrease")
    public Result<Void> decrease(@Valid @RequestBody PointsChangeRequest request) {
        userService.decreasePoints(request);
        return Result.ok();
    }

    @PostMapping("/internal/login-user")
    public Result<UserVO> loginUser(@Valid @RequestBody LoginUserRequest request) {
        return Result.ok(userService.getOrCreate(request));
    }
}
