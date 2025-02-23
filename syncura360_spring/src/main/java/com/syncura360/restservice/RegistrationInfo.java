package com.syncura360.restservice;

import com.syncura360.model.Hospital;
import com.syncura360.model.Staff;
import lombok.Data;

@Data
public class RegistrationInfo {

    private Staff staff;
    private Hospital hospital;

}
