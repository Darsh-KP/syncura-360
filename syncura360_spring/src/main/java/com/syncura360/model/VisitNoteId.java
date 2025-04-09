package com.syncura360.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.Hibernate;
import org.hibernate.annotations.ColumnDefault;

import java.io.Serial;
import java.time.LocalDateTime;
import java.util.Objects;

@NoArgsConstructor(force = true)
@Getter
@Embeddable
public class VisitNoteId implements java.io.Serializable {
    @Serial
    private static final long serialVersionUID = 5877623847837662145L;

    @Column(name = "hospital_id", nullable = false)
    private final Integer hospitalId;

    @Column(name = "patient_id", nullable = false)
    private final Integer patientId;

    @Column(name = "visit_admission_date_time", nullable = false)
    private final LocalDateTime visitAdmissionDateTime;

    @ColumnDefault("current_timestamp()")
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    public VisitNoteId(Integer hospitalId, Integer patientId, LocalDateTime visitAdmissionDateTime) {
        this.hospitalId = hospitalId;
        this.patientId = patientId;
        this.visitAdmissionDateTime = visitAdmissionDateTime;
    }

    @SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        VisitNoteId entity = (VisitNoteId) o;
        return Objects.equals(this.createdAt, entity.createdAt) &&
                Objects.equals(this.hospitalId, entity.hospitalId) &&
                Objects.equals(this.patientId, entity.patientId) &&
                Objects.equals(this.visitAdmissionDateTime, entity.visitAdmissionDateTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(createdAt, hospitalId, patientId, visitAdmissionDateTime);
    }

}