package com.syncura360.dto;

import com.syncura360.model.Hospital;
import com.syncura360.model.Staff;
import lombok.Data;

@Data
public class RegistrationInfo {

    private Staff staff;
    private Hospital hospital;

}
