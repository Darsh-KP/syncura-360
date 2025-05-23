package com.syncura360.dto.Patient;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * DTO for fetching a patient's details, including personal information such as name, date of birth,
 * contact information, and address.
 *
 * @author Darsh-KP
 */
@AllArgsConstructor
@Getter
public class PatientViewFetchDTO {
    private int patientId;
    private String firstName;
    private String lastName;
    private String dateOfBirth;
    private String gender;
    private String phone;
    private String addressLine1;
    private String addressLine2;
    private String city;
    private String state;
    private String postal;
    private String country;
}