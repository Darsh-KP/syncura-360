package com.syncura360.dto.Visit;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeleteRoomDTO {

    @NotNull(message="Patient id cannot be null.")
    private int patientID;

    @NotNull(message="Admission date time cannot be null.")
    private String visitAdmissionDateTime;

}
