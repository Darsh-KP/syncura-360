package com.syncura360.repository;

import com.syncura360.model.Room;
import com.syncura360.model.RoomId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RoomRepository extends JpaRepository<Room, RoomId> {
    boolean existsById_HospitalIdAndId_RoomName(Integer hospitalId, String roomName);

    Optional<Room> findById_HospitalIdAndId_RoomName(Integer hospitalId, String roomName);

    List<Room> findById_HospitalId(Integer hospitalId);
}
