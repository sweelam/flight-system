package com.flight.booking.entity;

import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;

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

    @Type(JsonType.class)
    @Column(columnDefinition = "json")
    private String payload;

    private String status;

    private String topic;
}