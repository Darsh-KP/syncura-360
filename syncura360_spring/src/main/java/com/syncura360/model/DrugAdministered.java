package com.syncura360.model;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(force = true)
@Getter
@EqualsAndHashCode(of = "id")
@Entity
@Table(name = "DrugAdministered", schema = "syncura360")
public class DrugAdministered {
    @EmbeddedId
    private final DrugAdministeredId id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumns({
            @JoinColumn(name = "hospital_id", referencedColumnName = "hospital_id", nullable = false, insertable = false, updatable = false),
            @JoinColumn(name = "patient_id", referencedColumnName = "patient_id", nullable = false, insertable = false, updatable = false),
            @JoinColumn(name = "visit_admission_date_time", referencedColumnName = "admission_date_time", nullable = false, insertable = false, updatable = false),
    })
    private Visit visit;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumns({
            @JoinColumn(name = "hospital_id", referencedColumnName = "hospital_id", nullable = false, insertable = false, updatable = false),
            @JoinColumn(name = "drug_ndc", referencedColumnName = "ndc", nullable = false, insertable = false, updatable = false),
    })
    private final Drug drug;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "administered_by", nullable = false, insertable = false, updatable = false)
    private final Staff administeredBy;

    public DrugAdministered(DrugAdministeredId id, Drug drug, Staff administeredBy) {
        this.id = id;
        this.drug = drug;
        this.administeredBy = administeredBy;
    }
}