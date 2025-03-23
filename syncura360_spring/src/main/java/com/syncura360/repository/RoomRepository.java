package com.syncura360.repository;

import com.syncura360.model.Room;
import com.syncura360.model.RoomId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoomRepository extends JpaRepository<Room, RoomId> {
    boolean existsById_HospitalIdAndId_RoomName(Integer hospitalId, String roomName);
}
