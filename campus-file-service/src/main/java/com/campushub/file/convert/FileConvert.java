package com.campushub.file.convert;

import com.campushub.file.vo.FileRecordVO;
import com.campushub.file.vo.FileUploadVO;

/**
 * Static file converters.
 */
public final class FileConvert {
    private FileConvert() {
    }

    /**
     * Converts a file record VO to upload VO.
     *
     * @param record file record
     * @return upload VO
     */
    public static FileUploadVO toUploadVO(FileRecordVO record) {
        return FileUploadVO.from(record);
    }
}
