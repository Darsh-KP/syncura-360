package com.example.syncura360_spring.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "Hospital", schema = "syncura360")
public class Hospital {
    @Id
    @Column(name = "hospital_id", nullable = false)
    private Integer id;

    @Column(name = "hospital_name", nullable = false)
    private String hospitalName;

    @Column(name = "address_line_1", nullable = false)
    private String addressLine1;

    @Column(name = "address_line_2")
    private String addressLine2;

    @Column(name = "city", nullable = false, length = 100)
    private String city;

    @Column(name = "state", nullable = false, length = 100)
    private String state;

    @Column(name = "postal", nullable = false, length = 20)
    private String postal;

    @Column(name = "telephone", length = 15)
    private String telephone;

    @Column(name = "type", length = 50)
    private String type;

    @Lob
    @Column(name = "trauma_level")
    private String traumaLevel;

    @Column(name = "has_helipad", nullable = false)
    private Boolean hasHelipad = false;

}