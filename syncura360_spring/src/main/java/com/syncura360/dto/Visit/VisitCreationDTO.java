package com.syncura360.dto.Visit;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class VisitCreationDTO {

    @NotNull(message = "Patient id required.")
    private int patientID;

    @Size(max = 255, message = "Max reason is 255 characters.")
    private String reasonForVisit;

}
