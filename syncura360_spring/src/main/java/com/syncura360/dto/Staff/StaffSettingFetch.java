package com.syncura360.dto.Staff;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * DTO for fetching staff setting details including personal, contact, and professional information.
 *
 * @author Darsh-KP
 */
@AllArgsConstructor
@Getter
public class StaffSettingFetch {
    private String username;
    private String role;
    private String firstName;
    private String lastName;
    private String email;
    private String dateOfBirth;
    private String phone;
    private String addressLine1;
    private String addressLine2;
    private String city;
    private String state;
    private String postal;
    private String country;
    private String specialty;
    private Integer yearsExperience;
}
