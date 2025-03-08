package com.syncura360.repository;

import com.syncura360.model.Hospital;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HospitalRepository extends JpaRepository<Hospital, Integer> {

    List<Hospital> findByName(String username);

    List<Hospital> findByTelephone(String phone);

    List<Hospital> addressLine1(String address);

}
