package com.syncura360.dto.Drug;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

@AllArgsConstructor
@Getter
public class DrugFetchDTO {
    private Integer ndc;
    private String name;
    private String category;
    private String description;
    private String strength;
    private Integer ppq;
    private Integer quantity;
    private BigDecimal price;
}
