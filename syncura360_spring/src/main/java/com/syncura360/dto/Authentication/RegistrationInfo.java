package com.syncura360.dto.Authentication;

import com.syncura360.dto.Hospital.HospitalCreationDto;
import com.syncura360.dto.Staff.StaffCreationDto;
import lombok.Data;

/**
 * DTO that models an incoming hospital registration request, including staff and hospital information.
 *
 * @author Benjamin Leiby
 */
@Data
public class RegistrationInfo {

    private StaffCreationDto staff;
    private HospitalCreationDto hospital;

}
