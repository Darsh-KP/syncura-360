package com.syncura360.controller;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class DeleteRoomDTO {

    @NotNull(message="Patient id cannot be null.")
    private int patientID;

    @NotNull(message="Admission date time cannot be null.")
    private String visitAdmissionDateTime;

}
