package com.syncura360.model;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Represents a service provided to a patient during a visit, including details like the staff who performed it and the service itself.
 *
 * @author Darsh-KP
 */
@NoArgsConstructor(force = true)
@Getter
@EqualsAndHashCode(of = "id")
@Entity
@Table(name = "ServiceProvided", schema = "syncura360")
public class ServiceProvided {
    @EmbeddedId
    private final ServiceProvidedId id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumns({
            @JoinColumn(name = "hospital_id", referencedColumnName = "hospital_id", nullable = false, insertable = false, updatable = false),
            @JoinColumn(name = "patient_id", referencedColumnName = "patient_id", nullable = false, insertable = false, updatable = false),
            @JoinColumn(name = "visit_admission_date_time", referencedColumnName = "admission_date_time", nullable = false, insertable = false, updatable = false),
    })
    private Visit visit;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "performed_by", nullable = false)
    private final Staff performedBy;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumns({
            @JoinColumn(name = "hospital_id", referencedColumnName = "hospital_id", nullable = false, insertable = false, updatable = false),
            @JoinColumn(name = "service_name", referencedColumnName = "name", nullable = false, insertable = false, updatable = false),
    })
    private final Service service;

    public ServiceProvided(ServiceProvidedId id, Staff performedBy, Service service) {
        this.id = id;
        this.performedBy = performedBy;
        this.service = service;
    }
}