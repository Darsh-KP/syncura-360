package com.syncura360.unorganized;

import java.time.LocalDate;
import java.util.List;

import lombok.Data;

@Data
public class StaffCreationRequest {

    @Data
    public static class StaffCreationDto {
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

    private List<StaffCreationDto> staff;
}

