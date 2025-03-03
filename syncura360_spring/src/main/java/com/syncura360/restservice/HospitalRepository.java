package com.syncura360.restservice;

import com.syncura360.model.Hospital;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HospitalRepository extends JpaRepository<Hospital, Integer> {

    List<Hospital> findByHospitalName(String username);

    List<Hospital> findByTelephone(String phone);

    List<Hospital> addressLine1(String address);

}
