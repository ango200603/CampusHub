package com.campushub.file.service;

import com.campushub.file.vo.FileRecordVO;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;

/**
 * Facade service for file APIs.
 */
public interface FileService {
    /**
     * Uploads a file for a user.
     *
     * @param userId user id
     * @param file multipart file
     * @return uploaded file record
     */
    FileRecordVO upload(Long userId, MultipartFile file);

    /**
     * Gets a file record.
     *
     * @param userId user id
     * @param id file id
     * @return file record
     */
    FileRecordVO get(Long userId, Long id);

    /**
     * Lists current user's files.
     *
     * @param userId user id
     * @return file list
     */
    List<FileRecordVO> my(Long userId);

    /**
     * Deletes a file record.
     *
     * @param userId user id
     * @param id file id
     */
    void delete(Long userId, Long id);
}
