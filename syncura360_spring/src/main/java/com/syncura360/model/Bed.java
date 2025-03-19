package com.syncura360.model;

import com.syncura360.model.enums.BedStatus;
import com.syncura360.model.enums.BedStatusConvertor;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.ColumnDefault;

@Data
@Entity
@Table(name = "Bed", schema = "syncura360")
public class Bed {
    @EmbeddedId
    private BedId id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumns({
            @JoinColumn(name = "hospital_id", referencedColumnName = "hospital_id", nullable = false, insertable = false, updatable = false),
            @JoinColumn(name = "room_no", referencedColumnName = "room_no", nullable = false, insertable = false, updatable = false)
    })
    private Room room;

    @Convert(converter = BedStatusConvertor.class)
    @ColumnDefault("Vacant")
    @Column(name = "status")
    private BedStatus status;
}