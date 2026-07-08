package com.campushub.user.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Points change request.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PointsChangeRequest {
    @NotNull
    private Long userId;

    @NotNull
    @Positive
    private Integer amount;
}
