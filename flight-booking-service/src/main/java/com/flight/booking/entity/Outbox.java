package com.flight.booking.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "outbox")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Outbox {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "outbox_id_gen")
    @SequenceGenerator(name = "outbox_id_gen", sequenceName = "outbox_id_seq", allocationSize = 1)
    private Long id;

    private String type;

    private String payload;

    private String status;
}