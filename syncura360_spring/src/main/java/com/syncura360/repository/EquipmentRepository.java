package com.syncura360.repository;

import com.syncura360.model.Equipment;
import com.syncura360.model.EquipmentId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EquipmentRepository extends JpaRepository<Equipment, EquipmentId> {
    List<Equipment> findById_HospitalIdAndId_RoomName(Integer hospitalId, String roomName);

    void deleteAllById_HospitalIdAndId_RoomName(Integer hospitalId, String roomName);
}
