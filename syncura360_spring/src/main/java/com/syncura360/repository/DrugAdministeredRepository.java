package com.syncura360.repository;

import com.syncura360.model.DrugAdministered;
import com.syncura360.model.DrugAdministeredId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DrugAdministeredRepository extends JpaRepository<DrugAdministered, DrugAdministeredId> {
}
