package com.syncura360.dto.Drug;

import lombok.AllArgsConstructor;

import java.math.BigDecimal;

@AllArgsConstructor
public class DrugFetchDTO {
    private Integer ndc;
    private String name;
    private String category;
    private String description;
    private String strength;
    private Integer quantity;
    private BigDecimal price;
}
