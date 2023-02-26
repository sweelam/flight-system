package com.flight.service;

import com.flight.dto.CustomerResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import static com.flight.builders.EmailBuilders.buildEmailEvent;

@Service
public class EmailService {
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final RestTemplate restTemplate;
    private final String topicName;

    public EmailService(KafkaTemplate<String, String> kafkaTemplate,
                        RestTemplate restTemplate,
                        @Value("${app.kafka.topic-name}") final String topicName) {
        this.topicName = topicName;
        this.restTemplate = restTemplate;
        this.kafkaTemplate = kafkaTemplate;
    }

    @Async
    public void sendEmail(String customerEmail, String flightName) {
        kafkaTemplate.send(topicName, buildEmailEvent(customerEmail, flightName));
    }

    public boolean customerFound(String customerEmail) {
        try {
            restTemplate.getForObject("http://localhost:8888/api/v1/customers/" + customerEmail, CustomerResponse.class);
        } catch (RestClientException e) {
            return false;
        }
        return true;
    }
}
