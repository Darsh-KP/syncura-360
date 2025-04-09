package com.syncura360.dto.Visit;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AddDrugDTO {

    @NotNull(message="Patient ID cannot be null.")
    private int patientID;

    @NotNull(message="Visit admission time cannot be null.")
    private String visitAdmissionDateTime;

    @NotNull(message="Administering staff cannot be null.")
    private String administeredBy;

    @NotNull(message="Drug cannot be null.")
    private Long drug;

    private int quantity;

}
