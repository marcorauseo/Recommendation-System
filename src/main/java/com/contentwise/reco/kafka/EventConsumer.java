package com.contentwise.reco.kafka;

import com.contentwise.reco.dto.EventRequest;
import com.contentwise.reco.model.RatingEvent;
import com.contentwise.reco.model.ViewEvent;
import com.contentwise.reco.repository.MovieRepository;
import com.contentwise.reco.repository.RatingEventRepository;
import com.contentwise.reco.repository.UserRepository;
import com.contentwise.reco.repository.ViewEventRepository;
import com.contentwise.reco.service.InteractionService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventConsumer {



    private final ObjectMapper mapper;
    private final InteractionService service;

    @KafkaListener(
            topics = "${kafka.events.topic:event_stream}",
            groupId = "${spring.application.name}-group",
    containerFactory = "listenerFactory")
    @Transactional
    public void handle(@Payload String raw, ConsumerRecord<String, String> record) {
        try {
            EventRequest req = mapper.readValue(raw, EventRequest.class);
            service.ingestEvent(req);
            log.debug("Event ingested from Kafka offset {}: {}", record.offset(), req);
        } catch (Exception ex) {
            log.error("Failed to process message: {}", raw, ex);
            throw new IllegalStateException("Cannot parse/ingest event", ex);
        }
    }

}

