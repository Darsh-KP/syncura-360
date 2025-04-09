package com.syncura360.dto.Patient;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

/**
 * DTO that contains a list of patient details ({@link PatientViewFetchDTO}) for fetching multiple patient records.
 *
 * @author Darsh-KP
 */
@AllArgsConstructor
@Getter
public class PatientViewFetchContainer {
    List<PatientViewFetchDTO> patients;
}