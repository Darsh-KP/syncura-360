package com.syncura360.repository;

import com.syncura360.model.RoomAssignment;
import com.syncura360.model.RoomAssignmentId;
import com.syncura360.model.Visit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface RoomAssignmentRepository extends JpaRepository<RoomAssignment, RoomAssignmentId> {

    @Query("SELECT ra FROM RoomAssignment ra " +
            "WHERE ra.id.patientId = :patientId " +
            "AND ra.id.hospitalId = :hospitalId " +
            "AND ra.isRemoved = FALSE")
    Optional<RoomAssignment> findCurrentAssignmentById(
            @Param("patientId") int patientId,
            @Param("hospitalId") int hospitalId
    );

    List<RoomAssignment> findAllByVisit(Visit visit);
}
