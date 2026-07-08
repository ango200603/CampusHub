package com.campushub.ai.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.campushub.ai.entity.AiTask;
import org.apache.ibatis.annotations.Mapper;

/**
 * Mapper for AI task records.
 */
@Mapper
public interface AiTaskMapper extends BaseMapper<AiTask> {
}
