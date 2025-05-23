package com.syncura360.dto.Staff;

import lombok.Data;
import java.time.LocalDate;

/**
 * DTO for creating a new staff member, containing personal and contact information as well as role details.
 *
 * @author Benjamin Leiby
 */
@Data
public class StaffCreationDto {
    private String username;
    private String passwordHash;
    private String role;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String addressLine1;
    private String addressLine2;
    private String city;
    private String state;
    private String postal;
    private String country;
    private LocalDate dateOfBirth;
}