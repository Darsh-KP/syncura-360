package com.syncura360.model;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

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
    @Column(name = "is_removed", nullable = false)
    private final Boolean isRemoved;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumns({
            @JoinColumn(name = "hospital_id", referencedColumnName = "hospital_id", nullable = false, insertable = false, updatable = false),
            @JoinColumn(name = "room_name", referencedColumnName = "room_name", nullable = false, insertable = false, updatable = false),
    })
    private final Room room;

    public RoomAssignment(RoomAssignmentId id, Boolean isRemoved, Room room) {
        this.id = id;
        this.isRemoved = isRemoved;
        this.room = room;
    }
}