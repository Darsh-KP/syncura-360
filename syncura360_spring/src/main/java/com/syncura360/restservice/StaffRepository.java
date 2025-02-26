package com.syncura360.restservice;

import com.syncura360.model.Hospital;
import com.syncura360.model.Staff;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StaffRepository extends JpaRepository<Staff, Integer> {

    Optional<Staff> findByUsername(String username);

    List<Staff> findByEmail(String email);

    List<Staff> findByPhone(String email);

    List<Staff> findByWorksAt(Hospital worksAt);

}
