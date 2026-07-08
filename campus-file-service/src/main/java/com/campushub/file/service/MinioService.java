package com.campushub.file.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * Service for MinIO object operations.
 */
public interface MinioService {
    /**
     * Uploads an object to MinIO.
     *
     * @param objectKey target object key
     * @param file multipart file
     * @return public object URL
     */
    String upload(String objectKey, MultipartFile file);
}
