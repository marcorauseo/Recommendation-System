package com.contentwise.reco.dto;

import java.time.Instant;

public record ViewDto(Long movieId,
                      String movieTitle,
                      int viewPercent,
                      Instant ts) { }
