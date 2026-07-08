package com.campushub.notice.dto;

import com.campushub.notice.enums.NoticeTypeEnum;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Notice creation request.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NoticeCreateDTO {
    @NotNull
    private Long userId;
    @NotBlank
    private String title;
    private String content;
    @Builder.Default
    private NoticeTypeEnum type = NoticeTypeEnum.SYSTEM;
}
