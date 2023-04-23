package com.krasnov.brutus.kafka;

import org.springframework.beans.factory.annotation.Value;

public class KafkaProducerConfig {

    @Value("${group-id}")
    private String GROUP_ID;
    private String TOPIC_NAME;

    public static final String DEFAULT_GROUP = "consumer-group";


}
