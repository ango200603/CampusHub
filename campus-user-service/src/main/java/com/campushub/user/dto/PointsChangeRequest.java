package com.campushub.user.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class PointsChangeRequest {
    @NotNull
    private Long userId;

    @NotNull
    @Positive
    private Integer amount;
}
