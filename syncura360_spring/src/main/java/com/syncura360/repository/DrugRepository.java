package com.syncura360.repository;

import com.syncura360.model.Drug;
import com.syncura360.model.DrugId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DrugRepository extends JpaRepository<Drug, DrugId> {
    boolean existsById_HospitalIdAndId_Ndc(int hospitalId, int ndc);

    //Optional<Drug> findById_HospitalIdAndId_Ndc(int hospitalID, int ndc);
}
