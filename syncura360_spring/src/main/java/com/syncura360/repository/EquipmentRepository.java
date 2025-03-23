package com.syncura360.repository;

import com.syncura360.model.Equipment;
import com.syncura360.model.EquipmentId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EquipmentRepository extends JpaRepository<Equipment, EquipmentId> {

}
