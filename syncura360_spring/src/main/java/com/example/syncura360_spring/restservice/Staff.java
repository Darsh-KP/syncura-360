package com.example.syncura360_spring.restservice;

import lombok.Data;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class Staff {

    private long staffId;

    @NotBlank(message="First name required")
    private String fname;

    @NotBlank(message="Last name required")
    private String lname;

    @NotBlank(message="Password required")
    private String password;

    @NotNull(message="Role required")
    private Role role;

}