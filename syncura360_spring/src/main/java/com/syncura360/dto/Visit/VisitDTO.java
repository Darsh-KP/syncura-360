package com.syncura360.dto.Visit;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class VisitDTO {

    private int patientID;

    private String admissionDateTime;

    private String firstName;

    private String lastName;

    private String dateOfBirth;

    private String note;

    public VisitDTO() {}

}
