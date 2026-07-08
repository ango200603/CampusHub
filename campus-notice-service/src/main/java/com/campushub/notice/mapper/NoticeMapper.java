package com.campushub.notice.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.campushub.notice.entity.Notice;
import org.apache.ibatis.annotations.Mapper;

/**
 * Mapper for notices.
 */
@Mapper
public interface NoticeMapper extends BaseMapper<Notice> {
}
