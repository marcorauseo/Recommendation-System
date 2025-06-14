package com.contentwise.reco.controller;

import com.contentwise.reco.service.InteractionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/users/{userId}/events")
@RequiredArgsConstructor
public class UserInteractionController {

    private final InteractionService interactionService;

    @GetMapping
    public ResponseEntity<?> history(@PathVariable Long userId,
                                     @RequestParam(required = false) String type,
                                     @RequestParam(defaultValue = "0") int page,
                                     @RequestParam(defaultValue = "50") int size) {

        return ResponseEntity.ok(
                interactionService.history(userId, type, PageRequest.of(page, size)));
    }
}
