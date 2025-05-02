package com.syncura360.service;

import com.syncura360.dto.Visit.*;
import com.syncura360.dto.Drug.DrugFetchDTO;
import com.syncura360.dto.Service.ServiceDTO;
import com.syncura360.model.*;
import com.syncura360.model.enums.BedStatus;
import com.syncura360.model.enums.Role;
import com.syncura360.repository.*;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.*;

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
    RoomRepository roomRepository;
    @Autowired
    RoomAssignmentRepository roomAssignmentRepository;
    @Autowired
    BedRepository bedRepository;

    /**
     * Retrieve note associated with given visit.
     * @return String visitNote.
     */
    public String getNote(int hospitalId, int patientId, String admissionDateTime, boolean record) {

        Optional<Visit> optionalVisit;

        if (record) {
            optionalVisit = visitRepository.findRecordById(patientId, hospitalId, LocalDateTime.parse(admissionDateTime));
        } else {
            optionalVisit = visitRepository.findCurrentVisitById(patientId, hospitalId);
        }

        if (optionalVisit.isEmpty()) {
            throw new EntityNotFoundException("Patient visit not found.");
        }

        return optionalVisit.get().getVisitNote();
    }

    /**
     * Retrieve all visit events summarized in timeline.
     * @return TimelineElementDTO
     */
    public List<TimelineElementDTO> getTimeline(int hospitalId, int patientId, String admissionDateTime, boolean record) {

        Optional<Visit> optionalVisit;

        if (record) {
            optionalVisit = visitRepository.findRecordById(patientId, hospitalId, LocalDateTime.parse(admissionDateTime));
        } else {
            optionalVisit = visitRepository.findCurrentVisitById(patientId, hospitalId);
        }

        if (optionalVisit.isEmpty()) {
            throw new EntityNotFoundException("Patient visit not found.");
        }

        Visit visit = optionalVisit.get();
        List<TimelineElementDTO> timeline = new ArrayList<>();

        // create admitted event

        timeline.add(new TimelineElementDTO(
            visit.getId().getAdmissionDateTime().toString(),
            "Patient Admittance",
            "Reason: " + visit.getReasonForVisit()
        ));

        // get services associated with visit

        List<ServiceProvided> servicesProvided = serviceProvidedRepository.findAllByVisit(visit);

        for (ServiceProvided serviceProvided : servicesProvided) {
            timeline.add(new TimelineElementDTO(
                serviceProvided.getId().getProvidedAt().toString(),
                serviceProvided.getServiceName() + " Performed",
                "Performed by: " + serviceProvided.getPerformedBy().getFirstName()
                + " " + serviceProvided.getPerformedBy().getLastName()
                + ". Price: $" + serviceProvided.getService().getCost()
                + ". Category: " + serviceProvided.getService().getCategory() + ". "
            ));
        }

        // get drugs associated with visit

        List<DrugAdministered> drugsAdministered = drugAdministeredRepository.findAllByVisit(visit);

        for (DrugAdministered drugAdministered : drugsAdministered) {
            timeline.add(new TimelineElementDTO(
                drugAdministered.getId().getAdministeredAt().toString(),
                drugAdministered.getDrug().getName() + " Administered",
                "Administered by: " + drugAdministered.getAdministeredBy().getFirstName()
                + " " + drugAdministered.getAdministeredBy().getLastName()
                + ". Name: " + drugAdministered.getDrug().getName()
                + ". Strength: " + drugAdministered.getDrug().getStrength()
                + ". Quantity: " + drugAdministered.getQuantity()
                + ". Cost: $" + drugAdministered.getDrug().getPrice().longValue() * drugAdministered.getQuantity()
            ));
        }

        // get rooms associated with visit

        List<RoomAssignment> roomAssignments = roomAssignmentRepository.findAllByVisit(visit);

        for (RoomAssignment roomAssignment : roomAssignments) {
            timeline.add(new TimelineElementDTO(
                roomAssignment.getId().getAssignedAt().toString(),
                "Assigned to " + roomAssignment.getRoomName(),
                "Department: " + roomAssignment.getRoom().getDepartment()
            ));

            // If removed, create timeline element for this event.
            if (roomAssignment.getIsRemoved()) {
                timeline.add(new TimelineElementDTO(
                        roomAssignment.getRemovedAt().toString(),
                        "Removed from " + roomAssignment.getRoomName(),
                        "Department: " + roomAssignment.getRoom().getDepartment()
                ));
            }

        }

        // if record get discharge date

        if (record) {
            timeline.add(new TimelineElementDTO(
                visit.getDischargeDateTime().toString(),
                "Patient discharged.",
                "Note: " + visit.getVisitNote()
            ));
        }

        timeline.sort(Comparator.comparing(dto -> LocalDateTime.parse(dto.getDateTime())));
        return timeline;
    }

    /**
     * Attempt to start a new visit.
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
     * Discharge patient from visit. Sets discharged date, removes current room assignments if any exist.
     */
    @Transactional
    public void discharge(int hospitalId, DischargeDTO dischargeDTO) {

        Optional<Visit> optionalVisit = visitRepository.findCurrentVisitById(dischargeDTO.getPatientID(), hospitalId);
        if (optionalVisit.isEmpty()) {
            throw new EntityNotFoundException("Patient visit not found.");
        }

        // check for room assignment and remove assignment if present
        Optional<RoomAssignment> currentAssignment = roomAssignmentRepository.findCurrentAssignmentById(
                dischargeDTO.getPatientID(), hospitalId
        );

        if (currentAssignment.isPresent()) {
            removeRoom(hospitalId, new DeleteRoomDTO(dischargeDTO.getPatientID(), dischargeDTO.getVisitAdmissionDateTime()));
        }
        // else patient has already been removed from room, so do nothing

        Visit visit = optionalVisit.get();

        visit.setVisitSummary(dischargeDTO.getVisitSummary());
        visit.setDischargeDateTime(LocalDateTime.now());
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
    public void addDrug(int hospitalId, AddDrugDTO addDrugDTO) throws DateTimeParseException, EntityNotFoundException {

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
        } else if (drug.get().getQuantity() == 0) {
            throw new EntityNotFoundException("The drug is not currently in inventory.");
        }

        // set value to default if null int type or invalid quantity
        if (addDrugDTO.getQuantity() <= 0) { addDrugDTO.setQuantity(1); }

        // check if enough drug in inventory
        if (addDrugDTO.getQuantity() > drug.get().getQuantity()) {
            throw new EntityNotFoundException("Not enough in inventory to administer this amount.");
        }

        DrugAdministered drugAdministered = new DrugAdministered(
            drugAdministeredId, drug.get(), staff.get(), addDrugDTO.getQuantity()
        );

        drugAdministeredRepository.save(drugAdministered);

        // Once drug has been administered, decrement inventory count by the administered quantity.
        Drug drugEntity = drug.get();
        drugEntity.setQuantity(drugEntity.getQuantity() - addDrugDTO.getQuantity());
        drugRepository.save(drugEntity);
    }

    /**
     * Remove a patient from a room.
     */
    @Transactional
    public void removeRoom(int hospitalId, DeleteRoomDTO deleteRoomDTO) {

        Optional<Visit> visit = visitRepository.findCurrentVisitById(deleteRoomDTO.getPatientID(), hospitalId);
        if (visit.isEmpty()) {
            throw new EntityNotFoundException("Visit not found.");
        }

        Optional<RoomAssignment> currentAssignment = roomAssignmentRepository.findCurrentAssignmentById(
            deleteRoomDTO.getPatientID(), hospitalId
        );

        if (currentAssignment.isEmpty()) {
            throw new EntityNotFoundException("Patient is not assigned to a room.");
        }

        Optional<Room> room = roomRepository.findById(currentAssignment.get().getRoom().getId());

        if (room.isEmpty()) {
            throw new EntityNotFoundException("Room not found.");
        }

        List<Bed> occupiedBeds = bedRepository.findAllByRoomAndStatus(room.get(), BedStatus.Occupied);
        if (occupiedBeds.isEmpty()) {
            throw new EntityNotFoundException("No beds occupied.");
        }

        RoomAssignment entity = currentAssignment.get();
        entity.setIsRemoved(true);
        entity.setRemovedAt(LocalDateTime.now());
        roomAssignmentRepository.save(entity);

        Bed occupied = occupiedBeds.getFirst();
        occupied.setStatus(BedStatus.Vacant);
        bedRepository.save(occupied);
    }

    /**
     * Add a patient to a room.
     */
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

        Optional<RoomAssignment> currentAssignment = roomAssignmentRepository.findCurrentAssignmentById(
            addRoomDTO.getPatientID(), hospitalId
        );

        if (currentAssignment.isPresent()) {
            throw new EntityExistsException("Patient is already assigned to a room.");
        }

        List<Bed> availableBeds = bedRepository.findAllByRoomAndStatus(room.get(), BedStatus.Vacant);

        if (availableBeds.isEmpty()) {
            throw new EntityNotFoundException("No beds available");
        }

        Bed nextAvailable = availableBeds.getFirst();

        RoomAssignmentId roomAssignmentId = new RoomAssignmentId(
            hospitalId, addRoomDTO.getPatientID(),
            LocalDateTime.parse(addRoomDTO.getVisitAdmissionDateTime())
        );

        RoomAssignment roomAssignment = new RoomAssignment(roomAssignmentId, false, room.get());
        roomAssignmentRepository.save(roomAssignment);

        nextAvailable.setStatus(BedStatus.Occupied);
        bedRepository.save(nextAvailable);
    }

    /**
     * Edit the visitNote field.
     */
    @Transactional
    public void editNote(int hospitalId, NoteDTO noteDTO) {

        Optional<Visit> visit = visitRepository.findCurrentVisitById(noteDTO.getPatientID(), hospitalId);
        if (visit.isEmpty()) {
            throw new EntityNotFoundException("Visit not found.");
        }

        visit.get().setVisitNote(noteDTO.getNote());
        visitRepository.save(visit.get());
    }

    /**
     * Get a list of all visit records. Can be further queried individually.
     */
    public List<VisitDTO> getVisits(int hospitalId) throws NoSuchElementException {

        List<Visit> entities = visitRepository.findCurrentVisitsByHospitalId(hospitalId);

        if (entities.isEmpty()) {
            throw new NoSuchElementException("No visits found.");
        }

        List<VisitDTO> result = new ArrayList<>();

        for (Visit entity : entities) {

            Optional<PatientInfo> patientInfo = patientInfoRepository.findById(entity.getId().getPatientId());
            if (patientInfo.isEmpty()) {
                throw new NoSuchElementException("Patient not found.");
            }

            result.add(new VisitDTO(
                entity.getId().getPatientId(),
                entity.getId().getAdmissionDateTime().toString(),
                patientInfo.get().getFirstName(),
                patientInfo.get().getLastName(),
                patientInfo.get().getDateOfBirth().toString(),
                entity.getVisitNote()
            ));
        }

        return result;
    }

    /**
     * Get a list of all visit records. Can be further queried individually.
     */
    public List<RecordDTO> getRecords(int hospitalId) throws NoSuchElementException {

        List<Visit> entities = visitRepository.findRecordsByHospitalId(hospitalId);

        if (entities.isEmpty()) {
            throw new NoSuchElementException("No visits found.");
        }

        List<RecordDTO> result = new ArrayList<>();

        for (Visit entity : entities) {

            Optional<PatientInfo> patientInfo = patientInfoRepository.findById(entity.getId().getPatientId());
            if (patientInfo.isEmpty()) {
                throw new NoSuchElementException("Patient not found.");
            }

            result.add(new RecordDTO(
                    entity.getId().getPatientId(),
                    entity.getId().getAdmissionDateTime().toString(),
                    patientInfo.get().getFirstName(),
                    patientInfo.get().getLastName(),
                    patientInfo.get().getDateOfBirth().toString(),
                    entity.getVisitNote(),
                    entity.getDischargeDateTime().toString()
            ));
        }

        return result;
    }

    /**
     * Get the doctors available at the hospital.
     */
    public List<DoctorDTO> getDoctors(int hospitalId) throws NoSuchElementException {

        List<Staff> entities = staffRepository.findByHospitalAndRole(hospitalId, Role.Doctor);

        if (entities.isEmpty()) {
            throw new NoSuchElementException("No doctors found.");
        }

        List<DoctorDTO> result = new ArrayList<>();

        for (Staff entity : entities) {
            result.add(new DoctorDTO(
                entity.getUsername(),
                entity.getFirstName(),
                entity.getLastName()
            ));
        }

        return result;
    }

    /**
     * Get services available at the hospital.
     */
    public List<ServiceDTO> getServices(int hospitalId) throws NoSuchElementException {

        List<com.syncura360.model.Service> entities = serviceRepository.findByHospitalId(hospitalId);

        if (entities.isEmpty()) {
            throw new NoSuchElementException("No doctors found.");
        }

        List<ServiceDTO> result = new ArrayList<>();

        for (com.syncura360.model.Service entity : entities) {
            result.add(new ServiceDTO(
                entity.getId().getName(),
                entity.getCategory(),
                entity.getDescription(),
                entity.getCost()
            ));
        }


        return result;
    }

    /**
     * Get a list of available drugs at the hospital.
     */
    public List<DrugFetchDTO> getDrugs(int hospitalId) throws NoSuchElementException {

        List<Drug> entities = drugRepository.findAllAvailableAtHospital(hospitalId);

        if (entities.isEmpty()) {
            throw new NoSuchElementException("No drugs found.");
        }

        List<DrugFetchDTO> result = new ArrayList<>();

        for (Drug entity : entities) {
            result.add(new DrugFetchDTO(
                entity.getId().getNdc(),
                entity.getName(),
                entity.getCategory().toString(),
                entity.getDescription(),
                entity.getStrength(),
                entity.getPpq(),
                entity.getQuantity(),
                entity.getPrice()
            ));
        }

        return result;
    }

    /**
     * Get list of available rooms at hospital.
     */
    public List<RoomDTO> getRooms(int hospitalID) throws NoSuchElementException {

        List<Room> entities = roomRepository.findById_HospitalId(hospitalID);
        List<RoomDTO> result = new ArrayList<>();

        for (Room entity : entities) {
            List<Bed> availableBeds = bedRepository.findAllByRoomAndStatus(entity, BedStatus.Vacant);
            if (!availableBeds.isEmpty()) {
                result.add(new RoomDTO(
                    entity.getId().getRoomName(),
                    entity.getDepartment()
                ));
            }
        }

        if (result.isEmpty()) {
            throw new NoSuchElementException("No available rooms.");
        }

        return result;
    }
}