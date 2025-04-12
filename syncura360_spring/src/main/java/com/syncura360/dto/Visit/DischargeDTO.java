package com.syncura360.dto.Visit;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class DischargeDTO {

    @NotNull(message="Patient id cannot be null.")
    private int patientID;

    @NotNull(message="Admission date time cannot be null.")
    private String visitAdmissionDateTime;

    @NotNull(message="Visit summary cannot be null.")
    private String visitSummary;

}
