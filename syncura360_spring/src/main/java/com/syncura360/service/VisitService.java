package com.syncura360.service;

import com.syncura360.controller.AddNoteDTO;
import com.syncura360.controller.AddRoomDTO;
import com.syncura360.dto.Visit.AddDrugDTO;
import com.syncura360.dto.Visit.AddServiceDTO;
import com.syncura360.dto.Visit.VisitCreationDTO;
import com.syncura360.model.*;
import com.syncura360.repository.*;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
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
    @Autowired
    VisitNoteRepository visitNoteRepository;
    @Autowired
    RoomRepository roomRepository;
    @Autowired
    RoomAssignmentRepository roomAssignmentRepository;

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

        Optional<Visit> optionalVisit = visitRepository.findCurrentVisitById(visitCreationDTO.getPatientID(), hospitalId);
        if (optionalVisit.isPresent()) {
            throw new EntityExistsException("Patient visit is ongoing.");
        }

        VisitId id = new VisitId(hospitalId, patientId);
        Visit visit = new Visit(id, visitCreationDTO.getReasonForVisit());
        visitRepository.save(visit);
    }

    /**
     * Attempt to add a service to a given visit.
     * @param hospitalId Hospital the patient is visiting.
     * @param addServiceDTO DTO to model incoming service addition request.
     */
    @Transactional
    public void addService(int hospitalId, AddServiceDTO addServiceDTO) throws DateTimeParseException {

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
     * Attempt to add a drug to a given visit.
     * @param hospitalId Hospital the patient is visiting.
     * @param addDrugDTO DTO to model incoming drug addition request.
     */
    @Transactional
    public void addDrug(int hospitalId, AddDrugDTO addDrugDTO) throws DateTimeParseException {

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

    /**
     * Add a note to a given visit.
     * @param hospitalId Hospital the patient is visiting.
     * @param addNoteDTO DTO to model the incoming note addition request.
     */
    @Transactional
    public void addNote(int hospitalId, AddNoteDTO addNoteDTO) throws DateTimeParseException {

        Optional<Visit> visit = visitRepository.findCurrentVisitById(addNoteDTO.getPatientID(), hospitalId);
        if (visit.isEmpty()) {
            throw new EntityNotFoundException("Visit not found.");
        }

        VisitNoteId visitNoteId = new VisitNoteId(
            hospitalId,
            addNoteDTO.getPatientID(),
            LocalDateTime.parse(addNoteDTO.getVisitAdmissionDateTime())
        );

        VisitNote visitNote = new VisitNote(visitNoteId, addNoteDTO.getNote());
        visitNoteRepository.save(visitNote);
    }

    @Transactional
    public void addRoom(int hospitalId, AddRoomDTO addRoomDTO) throws DateTimeParseException {

        Optional<Visit> visit = visitRepository.findCurrentVisitById(addRoomDTO.getPatientID(), hospitalId);
        if (visit.isEmpty()) {
            throw new EntityNotFoundException("Visit not found.");
        }

        RoomId roomId = new RoomId(hospitalId, addRoomDTO.getRoomName());

        Optional<Room> room = roomRepository.findById(roomId);
        if (room.isEmpty()) {
            throw new EntityNotFoundException("Room not found.");
        }

        RoomAssignmentId roomAssignmentId = new RoomAssignmentId(
            hospitalId, addRoomDTO.getPatientID(),
            LocalDateTime.parse(addRoomDTO.getVisitAdmissionTime())
        );

        RoomAssignment roomAssignment = new RoomAssignment(roomAssignmentId, false, room.get());
        roomAssignmentRepository.save(roomAssignment);
    }

}
