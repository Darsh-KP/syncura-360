package com.syncura360.model;

import com.syncura360.model.enums.BedStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

/**
 * Entity representing a bed in a hospital, associated with a specific room and status.
 *
 * @author Darsh-KP
 */
@NoArgsConstructor(force = true)
@Getter
@Setter
@EqualsAndHashCode(of = "id")
@Entity
@Table(name = "Bed", schema = "syncura360")
public class Bed {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "bed_no", nullable = false)
    private Long id;

    @Setter(AccessLevel.NONE)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumns({
            @JoinColumn(name = "hospital_id", referencedColumnName = "hospital_id", nullable = false),
            @JoinColumn(name = "room_name", referencedColumnName = "room_name", nullable = false)
    })
    private Room room;

    @ColumnDefault("Vacant")
    @Column(name = "status", nullable = false)
    private BedStatus status;

    public Bed(Room room) {
        this.status = BedStatus.Vacant;
        this.room = room;
    }
}