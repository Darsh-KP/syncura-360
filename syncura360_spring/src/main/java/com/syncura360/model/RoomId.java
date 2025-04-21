package com.syncura360.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.Hibernate;

import java.io.Serial;
import java.util.Objects;

/**
 * Composite primary key class for identifying a room in a hospital, based on hospital ID and room name.
 *
 * @author Darsh-KP
 */
@NoArgsConstructor(force = true)
@Getter
@Setter
@Embeddable
public class RoomId implements java.io.Serializable {
    @Serial
    private static final long serialVersionUID = -2835522800506666135L;

    @Column(name = "hospital_id", nullable = false)
    private final Integer hospitalId;

    @Column(name = "room_name", nullable = false, length = 50)
    private final String roomName;

    public RoomId(Integer hospitalId, String roomName) {
        this.hospitalId = hospitalId;
        this.roomName = roomName.trim();
    }

    @SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        RoomId entity = (RoomId) o;
        return Objects.equals(this.roomName, entity.roomName) &&
                Objects.equals(this.hospitalId, entity.hospitalId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(roomName, hospitalId);
    }
}