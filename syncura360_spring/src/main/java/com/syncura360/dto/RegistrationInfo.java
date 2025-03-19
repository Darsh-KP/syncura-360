package com.syncura360.dto;

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
