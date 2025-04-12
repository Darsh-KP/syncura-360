package com.syncura360.dto.Visit;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class AddServiceDTO {

    @NotNull(message = "Patient id required.")
    private int patientID;

    @NotNull(message = "Admission time is required.")
    private String visitAdmissionDateTime;

    @NotNull(message = "Administering staff is required.")
    private String performedBy;

    @NotNull(message = "Service name is required.")
    private String service;


}
