package com.syncura360.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "Room", schema = "syncura360")
public class Room {
    @EmbeddedId
    private RoomId id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "hospital_id", referencedColumnName = "hospital_id", nullable = false, insertable = false, updatable = false)
    private Hospital hospital;

}