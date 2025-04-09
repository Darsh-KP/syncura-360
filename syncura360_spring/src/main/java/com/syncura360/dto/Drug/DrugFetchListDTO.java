package com.syncura360.dto.Drug;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

/**
 * DTO that contains a list of drug details ({@link DrugFetchDTO}) for fetching multiple drugs.
 *
 * @author Darsh-KP
 */
@AllArgsConstructor
@Getter
public class DrugFetchListDTO {
    private List<DrugFetchDTO> drugs;
}
