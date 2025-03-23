package com.syncura360.dto.Drug;

import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

@Getter
public class DrugFormDTO {
    @NotNull(message = "NDC is required.")
    private Integer ndc;

    @NotNull(message = "Name is required.")
    @Size(max = 250, message = "Max length for name is 250 characters.")
    private String name;

    private String category;

    private String description;

    @NotNull(message = "Strength is required.")
    @Size(max = 50, message = "Max length for strength is 50 characters.")
    private String strength;

    @NotNull(message = "Quantity is required.")
    @PositiveOrZero(message = "Quantity cannot be negative.")
    private Integer quantity;

    @NotNull(message = "Price is required.")
    @Digits(integer = 10, fraction = 2, message = "Price must have up to 10 digits before the decimal and 2 after.")
    private BigDecimal price;
}
