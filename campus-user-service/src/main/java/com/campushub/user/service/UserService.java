package com.campushub.user.service;

import com.campushub.user.dto.LoginUserRequest;
import com.campushub.user.dto.PointsChangeRequest;
import com.campushub.user.dto.UpdateProfileRequest;
import com.campushub.user.vo.UserVO;

public interface UserService {
    UserVO getOrCreate(LoginUserRequest request);

    UserVO getById(Long id);

    UserVO updateMe(Long userId, UpdateProfileRequest request);

    Integer getPoints(Long userId);

    void increasePoints(PointsChangeRequest request);

    void decreasePoints(PointsChangeRequest request);
}
