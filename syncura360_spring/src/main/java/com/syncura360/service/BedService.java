package com.syncura360.service;

import com.syncura360.model.Bed;
import com.syncura360.model.Room;
import com.syncura360.model.enums.BedStatus;
import com.syncura360.repository.BedRepository;
import org.springframework.stereotype.Service;

/**
 * Service class responsible for managing beds in rooms.
 *
 * @author Darsh-KP
 */
@Service
public class BedService {
    BedRepository bedRepository;

    /**
     * Constructor for initializing {@link BedService} with required dependencies.
     * Uses constructor injection for necessary components.
     *
     * @param bedRepository The repository used for bed operations.
     */
    public BedService(BedRepository bedRepository) {
        this.bedRepository = bedRepository;
    }

    /**
     * Updates the number of beds for a given {@link Room}.
     *
     * @param room The room to update the beds for.
     * @param beds The desired number of beds in the room.
     */
    public void updateBedsForRoom(Room room, Integer beds) {
        // Get total number of beds
        int totalBeds = bedRepository.countByRoom(room);

        // Check if number of beds even changes
        if (beds == totalBeds) return;

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

    /**
     * Deletes vacant beds from a given {@link Room}.
     *
     * @param room The room from which beds will be deleted.
     * @param bedsToDelete The number of vacant beds to delete.
     * @throws IllegalArgumentException If there are not enough vacant beds to delete.
     */
    public void deleteBedsForRoom(Room room, int bedsToDelete) {
        // Get the number of free beds
        int vacantBeds = bedRepository.countByRoomAndStatus(room, BedStatus.Vacant);

        // Check if there are enough vacant beds to remove
        if (bedsToDelete > vacantBeds) {
            throw new IllegalArgumentException("Not enough vacant beds available to delete.");
        }

        // Delete the beds
        bedRepository.deleteXVacantBedsInRoom(room.getId().getHospitalId(), room.getId().getRoomName(), bedsToDelete);
    }
}
