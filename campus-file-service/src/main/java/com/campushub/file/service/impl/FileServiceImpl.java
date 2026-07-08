package com.campushub.file.service.impl;

import com.campushub.file.service.FileRecordService;
import com.campushub.file.service.FileService;
import com.campushub.file.vo.FileRecordVO;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 * Delegating file service facade.
 */
@Service
@RequiredArgsConstructor
@SuppressWarnings("unused")
public class FileServiceImpl implements FileService {
    private final FileRecordService fileRecordService;

    /**
     * Uploads a file for a user.
     *
     * @param userId user id
     * @param file multipart file
     * @return uploaded file record
     */
    @Override
    public FileRecordVO upload(Long userId, MultipartFile file) {
        return fileRecordService.upload(userId, file);
    }

    /**
     * Gets a file record.
     *
     * @param userId user id
     * @param id file id
     * @return file record
     */
    @Override
    public FileRecordVO get(Long userId, Long id) {
        return fileRecordService.get(userId, id);
    }

    /**
     * Lists current user's files.
     *
     * @param userId user id
     * @return file list
     */
    @Override
    public List<FileRecordVO> my(Long userId) {
        return fileRecordService.my(userId);
    }

    /**
     * Deletes a file record.
     *
     * @param userId user id
     * @param id file id
     */
    @Override
    public void delete(Long userId, Long id) {
        fileRecordService.delete(userId, id);
    }
}
