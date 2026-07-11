package com.campushub.user.service;

import com.campushub.user.dto.LoginUserRequest;
import com.campushub.user.dto.PointsChangeRequest;
import com.campushub.user.dto.UpdateProfileRequest;
import com.campushub.user.vo.UserSummaryVO;
import com.campushub.user.vo.UserVO;
import java.util.List;

/**
 * User service contract.
 */
public interface UserService {
    /**
     * Returns an existing user or creates one.
     */
    UserVO getOrCreate(LoginUserRequest request);

    /**
     * Returns a user by id.
     */
    UserVO getById(Long id);

    /**
     * Returns summaries for a set of users in one query.
     */
    List<UserSummaryVO> getSummaries(List<Long> ids);

    /**
     * Updates the current user profile.
     */
    UserVO updateMe(Long userId, UpdateProfileRequest request);

    /**
     * Returns the user points balance.
     */
    Integer getPoints(Long userId);

    /**
     * Increases a user points balance.
     */
    void increasePoints(PointsChangeRequest request);

    /**
     * Decreases a user points balance.
     */
    void decreasePoints(PointsChangeRequest request);
}
