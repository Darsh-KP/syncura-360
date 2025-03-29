package com.syncura360.dto.Drug;

import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

@Getter
public class DrugUpdateDTO {
    @NotNull(message = "NDC is required.")
    private Long ndc;

    @NotNull(message = "Quantity is required.")
    @PositiveOrZero(message = "Quantity cannot be negative.")
    private Integer quantity;

    @NotNull(message = "Price is required.")
    @Digits(integer = 10, fraction = 2, message = "Price must have up to 10 digits before the decimal and 2 after.")
    private BigDecimal price;
}
