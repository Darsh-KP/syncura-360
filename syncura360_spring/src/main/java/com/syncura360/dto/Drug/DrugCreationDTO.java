package com.syncura360.dto.Drug;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class DrugCreationDTO {
    private Integer ndc;
    private String name;
    private String category;
    private String description;
    private String strength;
    private Integer quantity;
    private BigDecimal price;
}
