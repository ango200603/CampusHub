package com.campushub.user.controller;

import com.campushub.common.constant.CommonConstant;
import com.campushub.common.api.Result;
import com.campushub.user.dto.LoginUserRequest;
import com.campushub.user.dto.PointsChangeRequest;
import com.campushub.user.dto.UserProfileDTO;
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

/**
 * User API controller.
 */
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    /**
     * Returns the current user identity.
     */
    @GetMapping("/me")
    public Result<UserVO> me(@RequestHeader(CommonConstant.HEADER_USER_ID) Long userId) {
        return Result.ok(userService.getById(userId));
    }

    /**
     * Updates the current user profile.
     */
    @PutMapping("/me")
    public Result<UserVO> updateMe(@RequestHeader(CommonConstant.HEADER_USER_ID) Long userId,
                                   @Valid @RequestBody UserProfileDTO request) {
        return Result.ok(userService.updateMe(userId, request.toUpdateProfileRequest()));
    }

    /**
     * Returns a record by id.
     */
    @GetMapping("/{id}")
    public Result<UserVO> get(@PathVariable Long id) {
        return Result.ok(userService.getById(id));
    }

    /**
     * Returns the points balance.
     */
    @GetMapping("/points/me")
    public Result<Integer> points(@RequestHeader(CommonConstant.HEADER_USER_ID) Long userId) {
        return Result.ok(userService.getPoints(userId));
    }

    /**
     * Increases points.
     */
    @PostMapping("/points/increase")
    public Result<Void> increase(@Valid @RequestBody PointsChangeRequest request) {
        userService.increasePoints(request);
        return Result.ok();
    }

    /**
     * Decreases points.
     */
    @PostMapping("/points/decrease")
    public Result<Void> decrease(@Valid @RequestBody PointsChangeRequest request) {
        userService.decreasePoints(request);
        return Result.ok();
    }

    /**
     * Creates or returns a login user.
     */
    @PostMapping("/internal/login-user")
    public Result<UserVO> loginUser(@Valid @RequestBody LoginUserRequest request) {
        return Result.ok(userService.getOrCreate(request));
    }
}
