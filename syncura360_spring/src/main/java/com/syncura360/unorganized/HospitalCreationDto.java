package com.syncura360.unorganized;

import com.syncura360.model.enums.TraumaLevel;
import lombok.Data;

@Data
public class HospitalCreationDto {
    private String name;
    private String addressLine1;
    private String addressLine2;
    private String city;
    private String state;
    private String postal;
    private String telephone;
    private String type;
    private String traumaLevel;
    private boolean hasHelipad;
}
