package com.contentwise.reco.config;

import io.github.resilience4j.common.circuitbreaker.configuration.CircuitBreakerConfigCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class ResilienceConfig {
    @Bean
    public CircuitBreakerConfigCustomizer defaultCustomizer() {
        return CircuitBreakerConfigCustomizer.of("default", builder -> builder
                .failureRateThreshold(50)
                .waitDurationInOpenState(Duration.ofSeconds(5))
                .slidingWindowSize(20));
    }
}
