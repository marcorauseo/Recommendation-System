package com.contentwise.reco.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record EventDto(
        @NotNull Long userId,
        @NotNull Long movieId,
        @Min(1) @Max(5) Integer rating,
        @Min(0) @Max(100) Integer viewPercentage
) {}
