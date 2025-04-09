package com.syncura360.service;

import com.syncura360.dto.Visit.AddDrugDTO;
import com.syncura360.dto.Visit.AddServiceDTO;
import com.syncura360.dto.Visit.VisitCreationDTO;
import com.syncura360.model.*;
import com.syncura360.repository.*;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class VisitService {

    @Autowired
    VisitRepository visitRepository;
    @Autowired
    PatientInfoRepository patientInfoRepository;
    @Autowired
    StaffRepository staffRepository;
    @Autowired
    ServiceRepository serviceRepository;
    @Autowired
    ServiceProvidedRepository serviceProvidedRepository;
    @Autowired
    DrugRepository drugRepository;
    @Autowired
    DrugAdministeredRepository drugAdministeredRepository;

    /**
     * Attempt to create start a new visit.
     * @param hospitalId Hospital the patient is visiting.
     * @param visitCreationDTO DTO to model incoming visit creation request.
     */
    @Transactional
    public void createVisit(int hospitalId, VisitCreationDTO visitCreationDTO) {

        Integer patientId = visitCreationDTO.getPatientID();

        if (!patientInfoRepository.existsById(patientId)) {
            throw new EntityNotFoundException("Patient id does not exist");
        }

        VisitId id = new VisitId(hospitalId, patientId);
        Visit visit = new Visit(id, visitCreationDTO.getReasonForVisit());
        visitRepository.save(visit);
    }

    /**
     *
     * @param hospitalId
     * @param addServiceDTO
     */
    @Transactional
    public void addService(int hospitalId, AddServiceDTO addServiceDTO) {

        Optional<Visit> visit = visitRepository.findCurrentVisitById(addServiceDTO.getPatientID(), hospitalId);

        if (visit.isEmpty()) {
            throw new EntityNotFoundException("Visit not found.");
        }

        ServiceProvidedId serviceProvidedId = new ServiceProvidedId(
                hospitalId, addServiceDTO.getPatientID(),
                LocalDateTime.parse(addServiceDTO.getVisitAdmissionDateTime())
        );

        Optional<Staff> staff = staffRepository.findByUsername(addServiceDTO.getPerformedBy());
        if (staff.isEmpty() || staff.get().getWorksAt().getId() != hospitalId) {
            throw new EntityNotFoundException("Providing staff not found.");
        }

        ServiceId serviceId = new ServiceId(hospitalId, addServiceDTO.getService());

        Optional<com.syncura360.model.Service> service = serviceRepository.findById(serviceId);
        if (service.isEmpty() || service.get().getId().getHospitalId() != hospitalId) {
            throw new EntityNotFoundException("Service not found.");
        }

        ServiceProvided serviceProvided = new ServiceProvided(serviceProvidedId, staff.get(), service.get());
        serviceProvidedRepository.save(serviceProvided);
    }

    /**
     *
     * @param hospitalId
     * @param addDrugDTO
     */
    @Transactional
    public void addDrug(int hospitalId, AddDrugDTO addDrugDTO) {

        Optional<Visit> visit = visitRepository.findCurrentVisitById(addDrugDTO.getPatientID(), hospitalId);

        if (visit.isEmpty()) {
            throw new EntityNotFoundException("Visit not found.");
        }

        DrugAdministeredId drugAdministeredId = new DrugAdministeredId(
                hospitalId, addDrugDTO.getPatientID(),
                LocalDateTime.parse(addDrugDTO.getVisitAdmissionDateTime())
        );

        Optional<Staff> staff = staffRepository.findByUsername(addDrugDTO.getAdministeredBy());
        if (staff.isEmpty() || staff.get().getWorksAt().getId() != hospitalId) {
            throw new EntityNotFoundException("Providing staff not found.");
        }

        DrugId drugId = new DrugId(hospitalId, addDrugDTO.getDrug());

        Optional<Drug> drug = drugRepository.findById(drugId);
        if (drug.isEmpty()) {
            throw new EntityNotFoundException("Drug not found.");
        }

        DrugAdministered drugAdministered = new DrugAdministered(
                drugAdministeredId, drug.get(), staff.get()
        );

        drugAdministeredRepository.save(drugAdministered);
    }

}
