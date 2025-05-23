package com.syncura360.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.Hibernate;
import org.hibernate.annotations.ColumnDefault;
import org.springframework.cglib.core.Local;

import java.io.Serial;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Composite primary key class for identifying a room assignment for a patient during a visit, based on hospital, patient, visit admission time, and assignment time.
 *
 * @author Darsh-KP
 */
@NoArgsConstructor(force = true)
@Getter
@Embeddable
public class RoomAssignmentId implements java.io.Serializable {
    @Serial
    private static final long serialVersionUID = -3628808484688004088L;

    @Column(name = "hospital_id", nullable = false)
    private final Integer hospitalId;

    @Column(name = "patient_id", nullable = false)
    private final Integer patientId;

    @Column(name = "visit_admission_date_time", nullable = false)
    private final LocalDateTime visitAdmissionDateTime;

    @ColumnDefault("current_timestamp()")
    @Column(name = "assigned_at", nullable = false)
    private LocalDateTime assignedAt = LocalDateTime.now();

    public RoomAssignmentId(Integer hospitalId, Integer patientId, LocalDateTime visitAdmissionDateTime) {
        this.hospitalId = hospitalId;
        this.patientId = patientId;
        this.visitAdmissionDateTime = visitAdmissionDateTime;
    }

    @SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        RoomAssignmentId entity = (RoomAssignmentId) o;
        return Objects.equals(this.hospitalId, entity.hospitalId) &&
                Objects.equals(this.patientId, entity.patientId) &&
                Objects.equals(this.visitAdmissionDateTime, entity.visitAdmissionDateTime) &&
                Objects.equals(this.assignedAt, entity.assignedAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(hospitalId, patientId, visitAdmissionDateTime, assignedAt);
    }
}