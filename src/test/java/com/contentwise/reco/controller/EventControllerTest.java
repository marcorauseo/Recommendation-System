package com.contentwise.reco.controller;

import com.contentwise.reco.dto.EventRequest;
import com.contentwise.reco.dto.EventType;
import com.contentwise.reco.kafka.EventProducer;
import com.contentwise.reco.service.InteractionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(EventController.class)
class EventControllerTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper mapper;

    @MockBean InteractionService interactionService;
    @MockBean EventProducer eventProducer;

    @Test
    void ingestReturnsAccepted() throws Exception {
        EventRequest req = EventRequest.builder()
                .userId(1L)
                .movieId(2L)
                .type(EventType.RATING)
                .rating(4)
                .build();

        mockMvc.perform(post("/v1/events")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(req)))
                .andExpect(status().isAccepted());

        verify(interactionService).ingestEvent(any(EventRequest.class));
        verify(eventProducer).send(eq("events"), eq("1-2"), anyString());
    }
}
