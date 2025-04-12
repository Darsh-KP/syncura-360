package com.syncura360.repository;

import com.syncura360.model.Hospital;
import com.syncura360.model.Staff;
import com.syncura360.model.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for performing CRUD operations on the Staff entity.
 *
 * @author Benjamin Leiby
 */
public interface StaffRepository extends JpaRepository<Staff, String> {

    @Query("SELECT s from Staff s " +
            "WHERE s.worksAt.id = :hospitalId " +
            "AND s.role = :role")
    List<Staff> findByHospitalAndRole(
            @Param("hospitalId") int hospitalId,
            @Param("role") Role role
    );

    public interface StaffProjection {
        String getUsername();
        String getRole();
        String getFirstName();
        String getLastName();
        String getEmail();
        LocalDate getDateOfBirth();
        String getPhone();
        String getAddressLine1();
        String getAddressLine2();
        String getCity();
        String getState();
        String getPostal();
        String getCountry();
        String getSpecialty();
        Integer getYearsExperience();
    }

    Optional<Staff> findByUsername(String username);

    List<StaffProjection> findByWorksAt(Hospital worksAt);


}
