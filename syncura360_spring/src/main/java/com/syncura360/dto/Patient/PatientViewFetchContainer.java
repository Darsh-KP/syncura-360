package com.syncura360.dto.Patient;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
@Getter
public class PatientViewFetchContainer {
    List<PatientViewFetchDTO> patients;
}