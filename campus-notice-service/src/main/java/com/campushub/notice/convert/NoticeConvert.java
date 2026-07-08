package com.campushub.notice.convert;

import com.campushub.notice.entity.Notice;
import com.campushub.notice.vo.NoticeVO;

/**
 * Static notice converters.
 */
public final class NoticeConvert {
    private NoticeConvert() {
    }

    /**
     * Converts notice entity to VO.
     *
     * @param notice notice entity
     * @return notice VO
     */
    public static NoticeVO toVO(Notice notice) {
        return NoticeVO.from(notice);
    }
}
