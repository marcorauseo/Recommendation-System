package com.contentwise.reco.controller;

import com.contentwise.reco.dto.EventRequest;
import com.contentwise.reco.kafka.EventProducer;
import com.contentwise.reco.service.InteractionService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/events")
@RequiredArgsConstructor
public class EventController {

    private final InteractionService interactionService;
    private final EventProducer eventProducer;
    private final ObjectMapper mapper;

    @PostMapping
    public ResponseEntity<Void> ingest(@RequestBody @Valid EventRequest request) throws JsonProcessingException {
        interactionService.ingestEvent(request);

        String key = request.getUserId() + "-" + request.getMovieId();
        String json = mapper.writeValueAsString(request);
        eventProducer.send("events", key, json);

        return ResponseEntity.accepted().build();
    }
}
