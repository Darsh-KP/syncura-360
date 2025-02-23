package com.example.syncura360_spring.restservice;

import com.example.syncura360_spring.model.Hospital;
import com.example.syncura360_spring.model.Staff;
import lombok.Data;

@Data
public class RegistrationInfo {

    private Staff staff;
    private Hospital hospital;

}
