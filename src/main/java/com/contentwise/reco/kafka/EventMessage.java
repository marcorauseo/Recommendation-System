package com.contentwise.reco.kafka;

import com.contentwise.reco.dto.EventType;


public record EventMessage(
        Long userId,
        Long movieId,
        EventType type,
        Integer rating,
        Integer viewPercent
) {}
