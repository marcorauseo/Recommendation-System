/** DECOMMENTARE SE SI VUOLE TESTARE IN MANIERA SINCRONA QUELLA CHE FA L'event CONTROLLER CON I TOPIC**/
//package com.contentwise.reco.controller;
//
//import com.contentwise.reco.dto.EventRequest;
//import com.contentwise.reco.service.InteractionService;
//import jakarta.validation.Valid;
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//@RestController
//@RequestMapping("/v1/events")
//@RequiredArgsConstructor
//public class RestEventController {
//
//    private final InteractionService interactionService;
//
//    @PostMapping
//    public ResponseEntity<Void> ingest(@RequestBody @Valid EventRequest request) {
//        interactionService.ingestEvent(request);
//        return ResponseEntity.accepted().build();
//    }
//}
