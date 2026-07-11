package com.campushub.user.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.campushub.common.constant.RedisKeyConstant;
import com.campushub.common.exception.BusinessException;
import com.campushub.common.exception.ErrorCode;
import com.campushub.common.redis.RedisLock;
import com.campushub.user.dto.LoginUserRequest;
import com.campushub.user.dto.PointsChangeRequest;
import com.campushub.user.dto.UpdateProfileRequest;
import com.campushub.user.entity.User;
import com.campushub.user.enums.UserStatusEnum;
import com.campushub.user.mapper.UserMapper;
import com.campushub.user.service.UserService;
import com.campushub.user.vo.UserSummaryVO;
import com.campushub.user.vo.UserVO;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * User service implementation.
 */
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserMapper userMapper;
    private final RedisLock redisLock;
    private final StringRedisTemplate redisTemplate;

    /**
     * {@inheritDoc}
     */
    @Override
    public UserVO getOrCreate(LoginUserRequest request) {
        User existing = findByPhone(request.getPhone());
        if (existing != null) {
            return UserVO.from(existing);
        }
        User user = new User();
        user.setPhone(request.getPhone());
        user.setNickname("校园用户" + request.getPhone().substring(7));
        user.setAvatarUrl("");
        user.setPoints(100);
        user.setStatus(UserStatusEnum.ENABLED.code());
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        try {
            userMapper.insert(user);
        } catch (DuplicateKeyException ignored) {
            user = findByPhone(request.getPhone());
        }
        return UserVO.from(user);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UserVO getById(Long id) {
        User user = userMapper.selectById(id);
        if (user == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "用户不存在");
        }
        return UserVO.from(user);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<UserSummaryVO> getSummaries(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return List.of();
        }
        return userMapper.selectByIds(ids).stream().map(UserSummaryVO::from).toList();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UserVO updateMe(Long userId, UpdateProfileRequest request) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "用户不存在");
        }
        if (StringUtils.hasText(request.getNickname())) {
            user.setNickname(request.getNickname());
        }
        if (request.getAvatarUrl() != null) {
            user.setAvatarUrl(request.getAvatarUrl());
        }
        user.setUpdatedAt(LocalDateTime.now());
        userMapper.updateById(user);
        return UserVO.from(user);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer getPoints(Long userId) {
        String key = RedisKeyConstant.userPoints(userId);
        String cached = redisTemplate.opsForValue().get(key);
        if (cached != null) {
            return Integer.valueOf(cached);
        }
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "用户不存在");
        }
        redisTemplate.opsForValue().set(key, String.valueOf(user.getPoints()), Duration.ofMinutes(10));
        return user.getPoints();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void increasePoints(PointsChangeRequest request) {
        redisLock.execute(RedisKeyConstant.lockUserPoints(request.getUserId()), Duration.ofSeconds(5), () -> {
            int updated = userMapper.update(null, Wrappers.<User>lambdaUpdate()
                    .eq(User::getId, request.getUserId())
                    .setSql("points = points + " + request.getAmount())
                    .set(User::getUpdatedAt, LocalDateTime.now()));
            if (updated == 0) {
                throw new BusinessException(ErrorCode.NOT_FOUND, "用户不存在");
            }
            redisTemplate.delete(RedisKeyConstant.userPoints(request.getUserId()));
            return true;
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void decreasePoints(PointsChangeRequest request) {
        redisLock.execute(RedisKeyConstant.lockUserPoints(request.getUserId()), Duration.ofSeconds(5), () -> {
            int updated = userMapper.update(null, Wrappers.<User>lambdaUpdate()
                    .eq(User::getId, request.getUserId())
                    .ge(User::getPoints, request.getAmount())
                    .setSql("points = points - " + request.getAmount())
                    .set(User::getUpdatedAt, LocalDateTime.now()));
            if (updated == 0) {
                throw new BusinessException(ErrorCode.CONFLICT, "积分不足或用户不存在");
            }
            redisTemplate.delete(RedisKeyConstant.userPoints(request.getUserId()));
            return true;
        });
    }

    private User findByPhone(String phone) {
        return userMapper.selectOne(Wrappers.<User>lambdaQuery().eq(User::getPhone, phone).last("limit 1"));
    }
}
