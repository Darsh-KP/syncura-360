package com.syncura360.dto.Drug;

import lombok.Getter;

import jakarta.validation.constraints.NotNull;

/**
 * DTO for deleting a drug, containing the required National Drug Code (NDC).
 *
 * @author Darsh-KP
 */
@Getter
public class DrugDeletionDTO {
    @NotNull(message = "NDC is required.")
    private Long ndc;
}
