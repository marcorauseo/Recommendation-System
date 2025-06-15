package com.contentwise.reco.kafka;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EventProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;
    @Value("${topic.events:events}")
    private String topic;

    public void send(String topic, String key, String payload) {
        kafkaTemplate.send(topic, key, payload);
    }



}
