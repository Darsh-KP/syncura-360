package com.syncura360.repository;

import com.syncura360.model.Drug;
import com.syncura360.model.DrugId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DrugRepository extends JpaRepository<Drug, DrugId> {
    boolean existsById_HospitalIdAndId_Ndc(Integer hospitalId, Long ndc);

    Optional<Drug> findById_HospitalIdAndId_Ndc(Integer hospitalID, Long ndc);

    List<Drug> findAllById_HospitalId(Integer hospitalId);
}
