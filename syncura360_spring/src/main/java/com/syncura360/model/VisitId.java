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
public class VisitId implements java.io.Serializable {
    @Serial
    private static final long serialVersionUID = -6970363276565412586L;

    @Column(name = "hospital_id", nullable = false)
    private final Integer hospitalId;

    @Column(name = "patient_id", nullable = false)
    private final Integer patientId;

    @ColumnDefault("current_timestamp()")
    @Column(name = "admission_date_time", nullable = false)
    private LocalDateTime admissionDateTime;

    public VisitId(Integer hospitalId, Integer patientId) {
        this.hospitalId = hospitalId;
        this.patientId = patientId;
    }

    @SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        VisitId entity = (VisitId) o;
        return Objects.equals(this.admissionDateTime, entity.admissionDateTime) &&
                Objects.equals(this.hospitalId, entity.hospitalId) &&
                Objects.equals(this.patientId, entity.patientId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(admissionDateTime, hospitalId, patientId);
    }
}