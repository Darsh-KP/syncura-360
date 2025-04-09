package com.syncura360.service;

import com.syncura360.controller.VisitCreationDTO;
import com.syncura360.model.Visit;
import com.syncura360.model.VisitId;
import com.syncura360.repository.PatientInfoRepository;
import com.syncura360.repository.VisitRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class VisitService {

    @Autowired
    VisitRepository visitRepository;
    @Autowired
    PatientInfoRepository patientInfoRepository;

    /**
     * Attempt to create start a new visit.
     * @param hospitalId Hospital the patient is visiting.
     * @param visitCreationDTO DTO to model incoming visit creation request.
     */
    @Transactional
    public void createVisit(int hospitalId, VisitCreationDTO visitCreationDTO) {

        Integer patientId = visitCreationDTO.getPatientId();

        if (!patientInfoRepository.existsById(patientId)) {
            throw new EntityNotFoundException("Patient id does not exist");
        }

        LocalDateTime current = LocalDateTime.now();

        VisitId id = new VisitId(hospitalId, patientId);
        id.setAdmissionDateTime(current);
        Visit visit = new Visit(id, visitCreationDTO.getReasonForVisit());
        visitRepository.save(visit);
    }




}
