package com.syncura360.service;

import com.syncura360.model.Bed;
import com.syncura360.model.Room;
import com.syncura360.model.enums.BedStatus;
import com.syncura360.repository.BedRepository;
import org.springframework.stereotype.Service;

@Service
public class BedService {
    BedRepository bedRepository;

    public BedService(BedRepository bedRepository) {
        this.bedRepository = bedRepository;
    }

    public void updateBedsForRoom(Room room, Integer beds) {
        // Get total number of beds
        int totalBeds = bedRepository.countByRoom(room);

        // Check if beds needs to be added
        if (beds > totalBeds) {
            // Create each new bed
            for (int i = 0; i < (beds - totalBeds); i++) {
                // Create a new bed
                Bed bed = new Bed(room);

                // Save the new bed in the database
                bedRepository.save(bed);
            }
            return;
        }

        // Calculate how many beds to delete
        int bedsToDelete = totalBeds - beds;

        // Delete the beds
        deleteBedsForRoom(room, bedsToDelete);
    }

    public void deleteBedsForRoom(Room room, int bedsToDelete) {
        // Get the number of free beds
        int vacantBeds = bedRepository.countByRoomAndStatus(room, BedStatus.Vacant);

        // Check if there are enough vacant beds to remove
        if (bedsToDelete < vacantBeds) {
            throw new IllegalArgumentException("Not enough vacant beds available to delete.");
        }

        // Delete the beds
        bedRepository.deleteXVacantBedsInRoom(room.getId().getHospitalId(), room.getId().getRoomName(), bedsToDelete);
    }
}
