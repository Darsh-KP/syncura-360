package com.syncura360.dto.Drug;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
@Getter
public class DrugFetchListDTO {
    private List<DrugFetchDTO> drugs;
}
