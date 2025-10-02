package com.flight.booking.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "outbox")
public record Outbox(
        @Id
        @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "outbox_id_gen")
        @SequenceGenerator(name = "outbox_id_gen", sequenceName = "outbox_id_seq", allocationSize = 1)
        Long id,
        String type,
        String payload,
        String status
) {
    public Outbox(String type, String payload, String status) {
        this(null, type, payload, status);
    }
}
