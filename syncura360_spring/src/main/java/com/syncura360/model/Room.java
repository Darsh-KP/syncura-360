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

    public Room(RoomId id) {
        this.id = id;
    }
}