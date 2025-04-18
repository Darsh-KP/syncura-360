package com.syncura360.dto.Visit;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class RecordDTO extends VisitDTO {

    public RecordDTO(int patientID, String admissionDateTime, String firstName, String lastName, String dateOfBirth, String note, String dischargeDateTime) {
        super(patientID, admissionDateTime, firstName, lastName, dateOfBirth, note);
        this.dischargeDateTime = dischargeDateTime;
    }

    private String dischargeDateTime;

}
