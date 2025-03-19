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
public class EquipmentId implements java.io.Serializable {
    private static final long serialVersionUID = -3512790571575068026L;
    @Column(name = "hospital_id", nullable = false)
    private Integer hospitalId;

    @Column(name = "room_no", nullable = false)
    private Integer roomNo;

    @Column(name = "serial_no", nullable = false, length = 25)
    private String serialNo;

    @SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        EquipmentId entity = (EquipmentId) o;
        return Objects.equals(this.roomNo, entity.roomNo) &&
                Objects.equals(this.hospitalId, entity.hospitalId) &&
                Objects.equals(this.serialNo, entity.serialNo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(roomNo, hospitalId, serialNo);
    }
}