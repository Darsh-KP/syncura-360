package com.syncura360.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.Hibernate;

import java.io.Serial;
import java.util.Objects;

@NoArgsConstructor(force = true)
@Getter
@Setter
@Embeddable
public class ServiceId implements java.io.Serializable {
    @Serial
    private static final long serialVersionUID = -4605148606755063898L;

    @Column(name = "hospital_id", nullable = false)
    private Integer hospitalId;

    @Column(name = "name", nullable = false)
    private String name;

    public ServiceId(Integer hospitalId, String name) {
        this.hospitalId = hospitalId;
        this.name = name;
    }

    @SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        ServiceId entity = (ServiceId) o;
        return Objects.equals(this.hospitalId, entity.hospitalId) &&
                Objects.equals(this.name, entity.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(hospitalId, name);
    }
}