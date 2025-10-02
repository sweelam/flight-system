package com.flight.booking.repo;

import com.flight.booking.entity.Outbox;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OutboxRepo extends JpaRepository<Outbox, Long> {
}
