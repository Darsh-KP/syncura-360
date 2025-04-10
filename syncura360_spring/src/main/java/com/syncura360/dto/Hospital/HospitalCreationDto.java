package com.syncura360.dto.Hospital;

import lombok.Data;

/**
 * DTO for creating a new hospital with essential details such as name, address, contact information,
 * type, trauma level, and helipad availability.
 *
 * @author Benjamin Leiby
 */
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
