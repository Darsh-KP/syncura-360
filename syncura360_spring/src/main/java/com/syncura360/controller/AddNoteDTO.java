package com.syncura360.controller;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AddNoteDTO {

    @NotNull(message="Patient id is required.")
    private int patientID;

    @NotNull(message="Visit admission time is required.")
    private String visitAdmissionDateTime;

    @NotNull(message="Note is required.")
    private String note;

}
