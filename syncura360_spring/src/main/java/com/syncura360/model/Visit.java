package com.syncura360.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Represents a visit to a hospital, including details such as the patient, hospital, and reason for visit.
 *
 * @author Darsh-KP
 */
@NoArgsConstructor(force = true)
@Getter
@Setter
@EqualsAndHashCode(of = "id")
@Entity
@Table(name = "Visit", schema = "syncura360")
public class Visit {
    @EmbeddedId
    private final VisitId id;

    @Setter(AccessLevel.NONE)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "hospital_id", referencedColumnName = "hospital_id", nullable = false, insertable = false, updatable = false)
    private Hospital hospital;

    @Setter(AccessLevel.NONE)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "patient_id", referencedColumnName = "patient_id", nullable = false, insertable = false, updatable = false)
    private PatientInfo patient;

    @Column(name = "discharge_date_time")
    private LocalDateTime dischargeDateTime;

    @Column(name = "reason_for_visit", length = 65535)
    private String reasonForVisit;

    @Column(name = "visit_summary", length = 65535)
    private String visitSummary;

    @Column(name = "visit_note", length = 65535)
    private String visitNote;

    public Visit(VisitId id, String reasonForVisit) {
        this.id = id;
        this.reasonForVisit = reasonForVisit == null ? null : reasonForVisit.trim();
    }
}