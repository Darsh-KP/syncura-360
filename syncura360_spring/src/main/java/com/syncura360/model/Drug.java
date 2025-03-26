package com.syncura360.model;

import com.syncura360.model.enums.DrugCategory;
import com.syncura360.model.enums.DrugCategoryConvertor;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import java.math.BigDecimal;

@NoArgsConstructor(force = true)
@Getter
@Setter
@EqualsAndHashCode(of = "id")
@Entity
@Table(name = "Drug", schema = "syncura360")
public class Drug {
    @EmbeddedId
    private final DrugId id;

    @Setter(AccessLevel.NONE)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "hospital_id", referencedColumnName = "hospital_id", nullable = false, insertable = false, updatable = false)
    private Hospital hospital;

    @Column(name = "name", nullable = false)
    private String name;

    @Convert(converter = DrugCategoryConvertor.class)
    @Column(name = "category")
    private DrugCategory category;

    @Column(name = "description")
    private String description;

    @Column(name = "strength", length = 50)
    private String strength;

    @Column(name = "PPQ")
    private Integer ppq;

    @ColumnDefault("0")
    @Column(name = "quantity", nullable = false)
    private Integer quantity = 0;

    @Column(name = "price", nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    public Drug(DrugId id, String name, DrugCategory category, String description, String strength, Integer ppq, Integer quantity, BigDecimal price) {
        this.id = id;
        this.name = name == null ? null : name.trim();
        this.category = category;
        this.description = description == null ? null : description.trim();
        this.strength = strength == null ? null : strength.trim();

        if (ppq != null && ppq < 0) {
            throw new IllegalArgumentException("PPQ cannot be negative.");
        }
        this.ppq = ppq;

        if (quantity < 0) {
            throw new IllegalArgumentException("Quantity cannot be negative.");
        }
        this.quantity = quantity;

        if (price.precision() > 10 || price.scale() > 2) {
            throw new IllegalArgumentException("Price must have a precision of 10 and a scale of 2.");
        }
        this.price = price;
    }
}