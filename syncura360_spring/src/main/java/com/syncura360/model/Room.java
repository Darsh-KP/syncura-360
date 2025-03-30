package com.syncura360.model;

import jakarta.persistence.*;
import lombok.*;

@NoArgsConstructor(force = true)
@Getter
@Setter
@EqualsAndHashCode(of = "id")
@Entity
@Table(name = "Room", schema = "syncura360")
public class Room {
    @EmbeddedId
    private final RoomId id;

    @Setter(AccessLevel.NONE)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "hospital_id", referencedColumnName = "hospital_id", nullable = false, insertable = false, updatable = false)
    private Hospital hospital;

    @Column(name = "department", nullable = false, length = 100)
    private String department;

    public Room(RoomId id, String department) {
        this.id = id;
        this.department = department.trim();
    }
}