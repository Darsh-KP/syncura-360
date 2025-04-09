package com.syncura360.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

/**
 * Represents a service offered by a hospital, with details like category, description, and cost.
 *
 * @author Darsh-KP
 */
@NoArgsConstructor(force = true)
@Getter
@Setter
@EqualsAndHashCode(of = "id")
@Entity
@Table(name = "Service", schema = "syncura360")
public class Service {
    @EmbeddedId
    private ServiceId id;

    @Setter(AccessLevel.NONE)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "hospital_id", referencedColumnName = "hospital_id", nullable = false, insertable = false, updatable = false)
    private Hospital hospital;

    @Column(name = "category", nullable = false, length = 1000)
    private String category;

    @Column(name = "description")
    private String description;

    @Column(name = "cost", nullable = false, precision = 15, scale = 2)
    private BigDecimal cost;

    public Service(ServiceId id, String category, String description, BigDecimal cost) {
        this.id = id;
        this.category = category;
        this.description = description == null ? null : description.trim();

        if (cost.precision() > 15 || cost.scale() > 2) {
            throw new IllegalArgumentException("Price must have a precision of 15 and a scale of 2.");
        }
        this.cost = cost;
    }
}