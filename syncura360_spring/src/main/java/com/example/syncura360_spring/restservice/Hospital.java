package com.example.syncura360_spring.restservice;

import lombok.Data;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class Hospital {

    private long hospitalId;

    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Phone is required")
    private String phone;

    @NotBlank(message = "Address is required")
    private String address;

    @NotNull(message = "Type is required")
    private HospitalType type;

    @NotNull(message = "Type is required")
    private TraumaLevel traumaLevel;

    @NotNull(message = "Helipad status is required")
    private boolean hasHelipad;

}
