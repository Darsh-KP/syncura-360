package com.syncura360.dto.Drug;

import lombok.Getter;

import javax.validation.constraints.NotNull;

@Getter
public class DrugDeletionDTO {
    @NotNull(message = "NDC is required.")
    private Integer ndc;
}
