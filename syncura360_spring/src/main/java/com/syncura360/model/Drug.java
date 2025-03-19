package com.syncura360.model;

import com.syncura360.model.enums.DrugCategory;
import com.syncura360.model.enums.DrugCategoryConvertor;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.ColumnDefault;

import java.math.BigDecimal;

@Data
@Entity
@Table(name = "Drug", schema = "syncura360")
public class Drug {
    @EmbeddedId
    private DrugId id;

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

    @Column(name = "strength", nullable = false, length = 50)
    private String strength;

    @ColumnDefault("0")
    @Column(name = "quantity")
    private Integer quantity;

    @Column(name = "price", nullable = false, precision = 10, scale = 2)
    private BigDecimal price;
}