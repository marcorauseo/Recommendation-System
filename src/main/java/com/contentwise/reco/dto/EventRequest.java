package com.contentwise.reco.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventRequest {

    @NotNull
    private Long userId;

    @NotNull
    private Long movieId;

    @NotNull
    private EventType type;

    @Min(1) @Max(5)
    private Integer rating;

    @Min(0) @Max(100)
    private Integer viewPercent;
}
