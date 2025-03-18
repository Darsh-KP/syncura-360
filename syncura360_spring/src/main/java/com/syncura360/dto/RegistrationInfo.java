package com.syncura360.dto;

import com.syncura360.model.Hospital;
import com.syncura360.model.Staff;
import com.syncura360.unorganized.HospitalCreationDto;
import com.syncura360.unorganized.StaffCreationDto;
import com.syncura360.unorganized.StaffCreationRequest;
import lombok.Data;

/**
 * DTO which models an incoming hospital registration request.
 * May need to be changed for security purposes (to prevent field injection).
 * @author Benjamin Leiby
 */
@Data
public class RegistrationInfo {

    private StaffCreationDto staff;
    private HospitalCreationDto hospital;

}
