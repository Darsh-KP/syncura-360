package com.syncura360.controller;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class VisitCreationDTO {

    @NotNull(message = "Patient id required.")
    private int patientId;

    @Size(max = 255, message = "Max reason is 255 characters.")
    private String reasonForVisit;

}
