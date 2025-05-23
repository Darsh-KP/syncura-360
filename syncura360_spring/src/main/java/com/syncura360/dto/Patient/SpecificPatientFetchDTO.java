package com.syncura360.dto.Patient;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * DTO for fetching detailed information of a specific patient, including personal data,
 * medical information, emergency contact, and address details.
 *
 * @author Darsh-KP
 */
@AllArgsConstructor
@Getter
public class SpecificPatientFetchDTO {
    private Integer patientId;
    private String firstName;
    private String lastName;
    private String dateOfBirth;
    private String gender;
    private String bloodType;
    private Integer height;
    private Integer weight;
    private String phone;
    private String addressLine1;
    private String addressLine2;
    private String city;
    private String state;
    private String postal;
    private String country;
    private String emergencyContactName;
    private String emergencyContactPhone;
    private String medicalNotes;
}