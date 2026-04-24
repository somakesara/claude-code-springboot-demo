package com.company.ordermanagement.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.util.backoff.ExponentialBackOff;

@Slf4j
@Configuration
@ConditionalOnBean(KafkaTemplate.class)
public class KafkaRetryConfig {

    // Activity 3 (Code Review) — Retry mechanism for payment events.
    //
    // ARCHITECTURE NOTE (what the AI review misses):
    // This adds consumer-level retry AFTER the payment gateway already has
    // its own retry logic. If the gateway returns a transient error and
    // the consumer retries, a second payment attempt may go to the gateway
    // while the first is still processing — causing duplicate charges.
    //
    // The correct pattern depends on the gateway's idempotency key support.
    // This implementation uses correlation_id as idempotency key, which
    // requires the gateway to honour it — verify before enabling in prod.

    @Bean
    public DefaultErrorHandler kafkaErrorHandler(
            KafkaTemplate<String, Object> kafkaTemplate) {

        // Exponential backoff: 1s → 2s → 4s → 8s → DLQ
        ExponentialBackOff backOff = new ExponentialBackOff(1000L, 2.0);
        backOff.setMaxAttempts(4);

        DeadLetterPublishingRecoverer recoverer = new DeadLetterPublishingRecoverer(
                kafkaTemplate,
                (record, ex) -> {
                    log.error("Message sent to DLQ: topic={}, key={}, error={}",
                            record.topic(), record.key(), ex.getMessage());
                    return new org.apache.kafka.common.TopicPartition(
                            record.topic() + ".dlq", 0);
                }
        );

        return new DefaultErrorHandler(recoverer, backOff);
    }
}
