package com.syncura360.repository;

import com.syncura360.model.Visit;
import com.syncura360.model.VisitId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface VisitRepository extends JpaRepository<Visit, VisitId> {

    @Query("SELECT vs FROM Visit vs " +
            "WHERE vs.id.patientId = :patientId " +
            "AND vs.id.hospitalId = :hospitalId " +
            "AND vs.dischargeDateTime IS NULL")
    Optional<Visit> findCurrentVisitById(
            @Param("patientId") int patientId,
            @Param("hospitalId") int hospitalId
    );

    @Query("SELECT vs FROM Visit vs " +
            "WHERE vs.id.hospitalId = :hospitalId " +
            "AND vs.dischargeDateTime is NULL")
    List<Visit> findCurrentVisitsByHospitalId(
            @Param("hospitalId") int hospitalId
    );

    @Query("SELECT vs FROM Visit vs " +
            "WHERE vs.id.patientId = :patientId " +
            "AND vs.id.admissionDateTime = :admissionDateTime " +
            "AND vs.id.hospitalId = :hospitalId " +
            "AND vs.dischargeDateTime IS NOT NULL")
    Optional<Visit> findRecordById(
            @Param("patientId") int patientId,
            @Param("hospitalId") int hospitalId,
            @Param("admissionDateTime") LocalDateTime admissionDateTime
    );

    @Query("SELECT vs FROM Visit vs " +
            "WHERE vs.id.hospitalId = :hospitalId " +
            "AND vs.dischargeDateTime IS NOT NULL")
    List<Visit> findRecordsByHospitalId(
            @Param("hospitalId") int hospitalId
    );

}
