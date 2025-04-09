package com.syncura360.repository;

import com.syncura360.model.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Repository interface for performing CRUD operations on the Service entity.
 *
 * @author Benjamin Leiby
 */
public interface ServiceRepository extends JpaRepository<Service, ServiceId> {
    List<Service> findByHospital(Hospital hospital);

    @Query("SELECT hs FROM Service hs " +
            "WHERE (:name IS NULL OR hs.id.name = :name) " +
            "AND (:category IS NULL OR hs.category = :category) " +
            "AND (hs.hospital.id = :hospitalId)")
    List<Service> findHospitalServicesByNameAndCategory(
            @Param("name") String name,
            @Param("category") String category,
            @Param("hospitalId") int hospitalId
    );
}
