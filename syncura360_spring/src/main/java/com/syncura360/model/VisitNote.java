package com.syncura360.model;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(force = true)
@Getter
@EqualsAndHashCode(of = "id")
@Entity
@Table(name = "VisitNote", schema = "syncura360")
public class VisitNote {
    @EmbeddedId
    private final VisitNoteId id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumns({
            @JoinColumn(name = "hospital_id", referencedColumnName = "hospital_id", nullable = false, insertable = false, updatable = false),
            @JoinColumn(name = "patient_id", referencedColumnName = "patient_id", nullable = false, insertable = false, updatable = false),
            @JoinColumn(name = "visit_admission_date_time", referencedColumnName = "admission_date_time", nullable = false, insertable = false, updatable = false),
    })
    private Visit visit;

    @Column(name = "note", nullable = false)
    private final String note;

    public VisitNote(VisitNoteId id, String note) {
        this.id = id;
        this.note = note.trim();
    }
}