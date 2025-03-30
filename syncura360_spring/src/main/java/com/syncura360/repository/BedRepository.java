package com.syncura360.repository;

import com.syncura360.model.Bed;
import com.syncura360.model.Room;
import com.syncura360.model.enums.BedStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BedRepository extends JpaRepository<Bed, Long> {
    Integer countByRoom(Room room);

    Integer countByRoomAndStatus(Room room, BedStatus bedStatus);

    @Modifying
    @Query(value = "DELETE FROM Bed b WHERE b.hospital_id = :hospitalId AND b.room_name = :roomName AND status = 'Vacant' LIMIT :x", nativeQuery = true)
    void deleteXVacantBedsInRoom(@Param("hospitalId") Integer hospitalId,@Param("roomName") String roomName,@Param("x") int x);
}
