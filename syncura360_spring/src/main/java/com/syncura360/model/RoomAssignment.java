package com.syncura360.model;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

/**
 * Entity representing a room assignment for a patient during a visit, including its unique identifier, associated room, and removal status.
 *
 * @author Darsh-KP
 */
@NoArgsConstructor(force = true)
@Getter
@EqualsAndHashCode(of = "id")
@Entity
@Table(name = "RoomAssignment", schema = "syncura360")
public class RoomAssignment {
    @EmbeddedId
    private RoomAssignmentId id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumns({
            @JoinColumn(name = "hospital_id", referencedColumnName = "hospital_id", nullable = false, insertable = false, updatable = false),
            @JoinColumn(name = "patient_id", referencedColumnName = "patient_id", nullable = false, insertable = false, updatable = false),
            @JoinColumn(name = "visit_admission_date_time", referencedColumnName = "admission_date_time", nullable = false, insertable = false, updatable = false),
    })
    private Visit visit;

    @ColumnDefault("0")
    @Setter
    @Column(name = "is_removed", nullable = false)
    private Boolean isRemoved;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumns({
            @JoinColumn(name = "hospital_id", referencedColumnName = "hospital_id", nullable = false, insertable = false, updatable = false),
            @JoinColumn(name = "room_name", referencedColumnName = "room_name", nullable = false, insertable = false, updatable = false),
    })
    private final Room room;

    @Column(name = "room_name", nullable = false)
    private final String roomName;

    public RoomAssignment(RoomAssignmentId id, Boolean isRemoved, Room room) {
        this.id = id;
        this.isRemoved = isRemoved;
        this.room = room;
        this.roomName = room.getId().getRoomName();
    }
}