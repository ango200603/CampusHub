package com.campushub.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.campushub.user.entity.User;
import org.apache.ibatis.annotations.Mapper;

/**
 * Mapper for users.
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {
}
