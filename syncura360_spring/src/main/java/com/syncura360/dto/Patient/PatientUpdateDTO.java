package com.syncura360.dto.Patient;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class PatientUpdateDTO {
    @NotNull(message = "Patient ID cannot be null.")
    private Integer id;

    @NotNull(message = "First name cannot be null.")
    @Size(max = 50, message = "First name cannot be longer than 50 characters.")
    private String firstName;

    @NotNull(message = "Last name cannot be null.")
    @Size(max = 50, message = "Last name cannot be longer than 50 characters.")
    private String lastName;

    @NotNull(message = "Date of birth cannot be null.")
    @Size(max = 20, message = "Date of birth cannot be longer than 20 characters.")
    private String dateOfBirth;

    @NotNull(message = "Gender cannot be null.")
    @Size(max = 20, message = "Gender cannot be longer than 20 characters.")
    private String gender;

    @Size(max = 5, message = "Blood type cannot be longer than 5 characters.")
    private String bloodType;

    @Max(value = 150, message = "Height cannot be greater than 150 inches.")
    private Integer height;

    @Max(value = 2000, message = "Weight cannot be greater than 2000 lbs.")
    private Integer weight;

    @NotNull(message = "Phone number cannot be null.")
    @Size(max = 15, message = "Phone number cannot be longer than 15 characters.")
    private String phone;

    @NotNull(message = "Address line 1 cannot be null.")
    @Size(max = 255, message = "Address line 1 cannot be longer than 255 characters.")
    private String addressLine1;

    @Size(max = 255, message = "Address line 2 cannot be longer than 255 characters.")
    private String addressLine2;

    @NotNull(message = "City cannot be null.")
    @Size(max = 100, message = "City cannot be longer than 100 characters.")
    private String city;

    @NotNull(message = "State cannot be null.")
    @Size(max = 100, message = "State cannot be longer than 100 characters.")
    private String state;

    @NotNull(message = "Postal code cannot be null.")
    @Size(max = 20, message = "Postal code cannot be longer than 20 characters.")
    private String postal;

    @NotNull(message = "Country cannot be null.")
    @Size(max = 100, message = "Country cannot be longer than 100 characters.")
    private String country;

    @Size(max = 100, message = "Emergency contact name cannot be longer than 100 characters.")
    private String emergencyContactName;

    @Size(max = 15, message = "Emergency contact phone cannot be longer than 15 characters.")
    private String emergencyContactPhone;
}