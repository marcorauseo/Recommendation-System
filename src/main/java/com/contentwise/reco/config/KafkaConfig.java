package com.contentwise.reco.config;

import jakarta.persistence.EntityManagerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.orm.jpa.JpaTransactionManager;


@Configuration
@EnableKafka
public class KafkaConfig {


    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, String> listenerFactory(
            ConsumerFactory<String, String> cf) {

        ConcurrentKafkaListenerContainerFactory<String, String> f =
                new ConcurrentKafkaListenerContainerFactory<>();
        f.setConsumerFactory(cf);

        return f;
    }

    @Bean
    @Primary
    public JpaTransactionManager transactionManager(EntityManagerFactory entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory);
    }

}
