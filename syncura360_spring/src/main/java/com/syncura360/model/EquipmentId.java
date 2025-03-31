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
public class EquipmentId implements java.io.Serializable {
    @Serial
    private static final long serialVersionUID = -3512790571575068026L;

    @Column(name = "hospital_id", nullable = false)
    private final Integer hospitalId;

    @Column(name = "room_name", nullable = false, length = 50)
    private final String roomName;

    @Column(name = "serial_no", nullable = false, length = 25)
    private final String serialNo;

    public EquipmentId(Integer hospitalId, String roomName, String serialNo) {
        this.hospitalId = hospitalId;
        this.roomName = roomName.trim();
        this.serialNo = serialNo.trim();
    }

    @SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        EquipmentId entity = (EquipmentId) o;
        return Objects.equals(this.roomName, entity.roomName) &&
                Objects.equals(this.hospitalId, entity.hospitalId) &&
                Objects.equals(this.serialNo, entity.serialNo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(roomName, hospitalId, serialNo);
    }
}