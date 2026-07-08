package com.campushub.common.entity;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Base fields shared by persistence entities.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@SuppressWarnings("unused")
public class BaseEntity {
    private Long id;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
