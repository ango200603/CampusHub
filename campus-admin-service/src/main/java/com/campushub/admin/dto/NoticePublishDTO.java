package com.campushub.admin.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Admin notice publish request.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@SuppressWarnings("unused")
public class NoticePublishDTO {
    @NotNull
    private Long userId;
    @NotBlank
    private String title;
    private String content;
}
