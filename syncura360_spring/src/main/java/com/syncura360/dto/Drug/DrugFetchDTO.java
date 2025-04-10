package com.syncura360.dto.Drug;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

/**
 * DTO for fetching drug details, including NDC, name, category, description, strength, and pricing information.
 *
 * @author Darsh-KP
 */
@AllArgsConstructor
@Getter
public class DrugFetchDTO {
    private Long ndc;
    private String name;
    private String category;
    private String description;
    private String strength;
    private Integer ppq;
    private Integer quantity;
    private BigDecimal price;
}
