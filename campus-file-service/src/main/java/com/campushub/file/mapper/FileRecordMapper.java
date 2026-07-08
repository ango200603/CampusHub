package com.campushub.file.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.campushub.file.entity.FileRecord;
import org.apache.ibatis.annotations.Mapper;

/**
 * Mapper for file records.
 */
@Mapper
public interface FileRecordMapper extends BaseMapper<FileRecord> {
}
