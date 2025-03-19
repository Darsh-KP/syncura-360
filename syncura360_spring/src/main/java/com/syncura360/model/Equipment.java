package com.syncura360.model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.ColumnDefault;

@Data
@Entity
@Table(name = "Equipment", schema = "syncura360")
public class Equipment {
    @EmbeddedId
    private EquipmentId id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumns({
            @JoinColumn(name = "hospital_id", referencedColumnName = "hospital_id", nullable = false, insertable = false, updatable = false),
            @JoinColumn(name = "room_no", referencedColumnName = "room_no", nullable = false, insertable = false, updatable = false)
    })
    private Room room;

    @Column(name = "name", nullable = false)
    private String name;

    @ColumnDefault("0")
    @Column(name = "under_maintenance")
    private Boolean underMaintenance;
}