package com.example.syncura360_spring.restservice;

import lombok.Data;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class Hospital {

    /*
    Hospital (hospitalId: int primary key, name: varchar(255),
    address: varchar(255), city: varchar(100), state: varchar(100),
    postal: varchar(20), telephone: varchar(15), type: varchar(50),
    traumaLevel: enum(‘Level  I’, ‘Level II’, ‘Level III’, ‘Level IV’),
    hasHelipad: boolean))
    */

    private int hospitalId;

    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Phone is required")
    private String phone;

    @NotBlank(message = "Address is required")
    private String address;

    private String address2;

    @NotBlank(message = "City is required")
    private String city;

    @NotBlank(message = "State is required")
    private String state;

    @NotNull(message = "Trauma Level is required")
    private TraumaLevel traumaLevel;

    @NotNull(message = "Helipad status is required")
    private boolean hasHelipad;

}
