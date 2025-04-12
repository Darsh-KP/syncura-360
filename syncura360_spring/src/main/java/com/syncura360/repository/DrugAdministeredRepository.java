package com.syncura360.repository;

import com.syncura360.model.DrugAdministered;
import com.syncura360.model.DrugAdministeredId;
import com.syncura360.model.Visit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DrugAdministeredRepository extends JpaRepository<DrugAdministered, DrugAdministeredId> {

    List<DrugAdministered> findAllByVisit(Visit visit);



}
