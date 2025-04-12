package com.syncura360.dto.Visit;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class NoteDTO {

    @NotNull(message="Patient id cannot be null.")
    private int patientID;

    @NotNull(message="Admission date time cannot be null.")
    private String visitAdmissionDateTime;

    @NotNull(message="Note cannot be null.")
    private String note;

}
