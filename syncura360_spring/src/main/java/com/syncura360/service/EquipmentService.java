package com.syncura360.service;

import com.syncura360.model.Equipment;
import com.syncura360.repository.EquipmentRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EquipmentService {
    EquipmentRepository equipmentRepository;

    public EquipmentService(EquipmentRepository equipmentRepository) {
        this.equipmentRepository = equipmentRepository;
    }

    public void setEquipmentsForRoom(List<Equipment> equipments) {

    }
}
