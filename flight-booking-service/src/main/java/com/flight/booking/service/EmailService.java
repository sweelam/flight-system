package com.flight.booking.service;

import com.flight.booking.builders.EmailBuilders;
import com.flight.booking.entity.Outbox;
import com.flight.booking.repo.OutboxRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Limit;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final OutboxRepo outboxRepo;

    @Scheduled(fixedDelay = 10000)     // every 10 seconds
    public void sendEmail() {
        List<Outbox> outboxes = outboxRepo.findAllByStatus("PENDING", Limit.of(10))
                .stream()
                .peek(outbox -> {
                    var event = EmailBuilders.buildEmailEvent(outbox.getPayload());

                    log.info("Sending notification event: \n {}", event);
                    kafkaTemplate.send(outbox.getTopic(), event);

                    outbox.setStatus("SENT");
                }).toList();

        outboxRepo.saveAll(outboxes);
    }
}
