package com.contentwise.reco.controller;

import com.contentwise.reco.dto.PlayEventDto;
import com.contentwise.reco.dto.RatingEventDto;
import com.contentwise.reco.model.PlayEvent;
import com.contentwise.reco.model.RatingEvent;
import com.contentwise.reco.service.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/events")
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;

//    @PostMapping("/rating")
//    public ResponseEntity<Void> rating(@RequestBody RatingEventDto dto) {
//        RatingEvent ratingEvent = RatingEvent.builder()
//                .id(dto.userId())
//                .movieId(dto.movieId())
//                .rating(dto.rating())
//                .build();
//        eventService.saveRating(ratingEvent);
//        return ResponseEntity.accepted().build();
//    }

    @PostMapping("/playback")
    public ResponseEntity<Void> playback(@RequestBody PlayEventDto dto) {
        PlayEvent playEvent = PlayEvent.builder()
                .userId(dto.userId())
                .movieId(dto.movieId())
                .position(dto.position())
                .build();
        eventService.savePlay(playEvent);
        return ResponseEntity.accepted().build();
    }
}
