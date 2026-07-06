package com.campushub.file.service;

import com.campushub.file.vo.FileRecordVO;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;

public interface FileRecordService {
    FileRecordVO upload(Long userId, MultipartFile file);

    FileRecordVO get(Long userId, Long id);

    List<FileRecordVO> my(Long userId);

    void delete(Long userId, Long id);
}
