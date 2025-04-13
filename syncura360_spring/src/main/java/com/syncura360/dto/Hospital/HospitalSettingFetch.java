package com.syncura360.dto.Hospital;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class HospitalSettingFetch {
    private String hospitalName;
    private String addressLine1;
    private String addressLine2;
    private String city;
    private String state;
    private String postal;
    private String telephone;
    private String type;
    private String traumaLevel;
    private Boolean hasHelipad;
}
