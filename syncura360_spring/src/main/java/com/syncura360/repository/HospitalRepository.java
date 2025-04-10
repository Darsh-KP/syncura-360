package com.syncura360.repository;

import com.syncura360.model.Hospital;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Repository interface for performing CRUD operations on the Hospital entity.
 *
 * @author Darsh-KP
 */
public interface HospitalRepository extends JpaRepository<Hospital, Integer> {
    List<Hospital> findByName(String username);

    List<Hospital> findByTelephone(String phone);

    List<Hospital> addressLine1(String address);
}
