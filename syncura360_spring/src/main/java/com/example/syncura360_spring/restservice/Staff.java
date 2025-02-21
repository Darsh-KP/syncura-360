package com.example.syncura360_spring.restservice;

import lombok.Data;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;

/*
Staff (staffId: int, worksAt: int, username: varchar(20) primary key,
    password:varchar(15), role: enum(‘Doctor’, ‘Nurse’, ‘Admin’, ‘Super_Admin’),
    email: varchar(50), dateOfBirth: date, phone: varchar(15), address: varchar(255),
    city: varchar(100), state: varchar(100), postal: varchar(20), country: varchar(100),
    specialty: varchar(100), expYears: int, primary key(staffId, worksAt), foreign key (worksAt) references Hospital(hospitalId))
*/

@Data
public class Staff {

    private int staffId;

    private int worksAt;

    @NotBlank(message="Username is required")
    private String username;

    @NotNull(message="Role is required")
    private Role role;

    @NotBlank(message="Name is required")
    private String name;

    @NotBlank(message="Password required")
    private String password;

    @NotBlank(message="Email required")
    private String email;

    @NotBlank(message="Phone required")
    private String phone;

    @NotBlank(message="Dob required")
    private Date dob;

    @NotBlank(message="Address required")
    private String address;

    @NotBlank(message="City required")
    private String city;

    @NotBlank(message="State required")
    private String state;

    @NotBlank(message="Postal required")
    private String postal;

    @NotBlank(message="Country required")
    private String country;

}