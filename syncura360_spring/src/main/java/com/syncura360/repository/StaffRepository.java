package com.syncura360.repository;

import com.syncura360.model.Hospital;
import com.syncura360.model.Staff;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface StaffRepository extends JpaRepository<Staff, Integer> {

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

    List<Staff> findByEmail(String email);

    List<Staff> findByPhone(String email);

    List<StaffProjection> findByWorksAt(Hospital worksAt);

}
