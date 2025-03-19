package com.syncura360.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.Hibernate;

import java.util.Objects;

@Getter
@Setter
@Embeddable
public class DrugId implements java.io.Serializable {
    private static final long serialVersionUID = 2419329460698846385L;
    @Column(name = "hospital_id", nullable = false)
    private Integer hospitalId;

    @Column(name = "ndc", nullable = false)
    private Integer ndc;

    @SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        DrugId entity = (DrugId) o;
        return Objects.equals(this.hospitalId, entity.hospitalId) &&
                Objects.equals(this.ndc, entity.ndc);
    }

    @Override
    public int hashCode() {
        return Objects.hash(hospitalId, ndc);
    }
}