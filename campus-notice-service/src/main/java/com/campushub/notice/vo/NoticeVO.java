package com.campushub.notice.vo;

import com.campushub.notice.entity.Notice;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class NoticeVO {
    private Long id;
    private String title;
    private String content;
    private Integer readStatus;
    private LocalDateTime createdAt;

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
