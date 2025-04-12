package com.syncura360.dto.Visit;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AddRoomDTO {

    @NotNull(message="Patient Id is required.")
    private int patientID;

    @NotNull(message="Admission time is required.")
    private String visitAdmissionDateTime;

    @NotNull(message="Room name is required.")
    private String roomName;

}
