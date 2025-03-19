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
public class RoomId implements java.io.Serializable {
    private static final long serialVersionUID = -2835522800506666135L;
    @Column(name = "hospital_id", nullable = false)
    private Integer hospitalId;

    @Column(name = "room_no", nullable = false)
    private Integer roomNo;

    @SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        RoomId entity = (RoomId) o;
        return Objects.equals(this.roomNo, entity.roomNo) &&
                Objects.equals(this.hospitalId, entity.hospitalId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(roomNo, hospitalId);
    }
}