package com.flight.booking.repo;

import com.flight.booking.entity.Outbox;
import org.springframework.data.domain.Limit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OutboxRepo extends JpaRepository<Outbox, Long> {
    List<Outbox> findAllByStatus(String status, Limit limit);
}
