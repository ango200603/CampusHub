package com.campushub.notice.vo;

import com.campushub.notice.entity.Notice;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Notice response object.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NoticeVO {
    private Long id;
    private String title;
    private String content;
    private Integer readStatus;
    private LocalDateTime createdAt;

    /**
     * Converts a notice entity to VO.
     *
     * @param notice notice entity
     * @return notice VO
     */
    public static NoticeVO from(Notice notice) {
        return NoticeVO.builder()
                .id(notice.getId())
                .title(notice.getTitle())
                .content(notice.getContent())
                .readStatus(notice.getReadStatus())
                .createdAt(notice.getCreatedAt())
                .build();
    }
}
