package com.campushub.file.service;

import com.campushub.file.vo.FileRecordVO;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;

/**
 * File service contract.
 */
public interface FileRecordService {
    /**
     * Uploads a file.
     */
    FileRecordVO upload(Long userId, MultipartFile file);

    /**
     * Returns a record by id.
     */
    FileRecordVO get(Long userId, Long id);

    /**
     * Returns records owned by the current user.
     */
    List<FileRecordVO> my(Long userId);

    /**
     * Deletes an existing record.
     */
    void delete(Long userId, Long id);
}
