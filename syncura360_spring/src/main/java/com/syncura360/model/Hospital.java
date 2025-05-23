package com.syncura360.model;

import com.syncura360.model.enums.TraumaLevel;
import com.syncura360.model.enums.TraumaLevelConvertor;
import jakarta.persistence.*;
import lombok.Data;

/**
 * Entity representing a hospital, including its details such as name, address, contact information, trauma level, and facilities.
 *
 * @author Darsh-KP
 */
@Data
@Entity
@Table(name = "Hospital")
public class Hospital {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "hospital_id", nullable = false)
    private Integer id;

    @Column(name = "hospital_name", nullable = false)
    private String name;

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

    @Convert(converter = TraumaLevelConvertor.class)
    @Column(name = "trauma_level")
    private TraumaLevel traumaLevel;

    @Column(name = "has_helipad", nullable = false)
    private Boolean hasHelipad = false;
}