package com.syncura360.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

@NoArgsConstructor(force = true)
@Getter
@Setter
@EqualsAndHashCode(of = "id")
@Entity
@Table(name = "Equipment", schema = "syncura360")
public class Equipment {
    @EmbeddedId
    private final EquipmentId id;

    @Setter(AccessLevel.NONE)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumns({
            @JoinColumn(name = "hospital_id", referencedColumnName = "hospital_id", nullable = false, insertable = false, updatable = false),
            @JoinColumn(name = "room_name", referencedColumnName = "room_name", nullable = false, insertable = false, updatable = false)
    })
    private Room room;

    @Column(name = "name", nullable = false)
    private String name;

    @ColumnDefault("0")
    @Column(name = "under_maintenance", nullable = false)
    private Boolean underMaintenance;

    public Equipment(EquipmentId id, String name, Boolean underMaintenance) {
        this.id = id;
        this.name = name;
        this.underMaintenance = underMaintenance;
    }
}