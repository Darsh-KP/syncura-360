package com.syncura360.dto.Service;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ServiceDTO {

    private String name;
    private String category;
    private String description;
    private BigDecimal cost;

}
