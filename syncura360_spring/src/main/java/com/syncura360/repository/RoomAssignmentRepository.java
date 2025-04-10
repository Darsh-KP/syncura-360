package com.syncura360.repository;

import com.syncura360.model.RoomAssignment;
import com.syncura360.model.RoomAssignmentId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoomAssignmentRepository extends JpaRepository<RoomAssignment, RoomAssignmentId> {
}
