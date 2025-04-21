package com.syncura360.repository;

import com.syncura360.model.Bed;
import com.syncura360.model.Room;
import com.syncura360.model.enums.BedStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Repository interface for performing CRUD operations on the Bed entity.
 *
 * @author Darsh-KP
 */
public interface BedRepository extends JpaRepository<Bed, Long> {
    Integer countByRoom(Room room);

    Integer countByRoomAndStatus(Room room, BedStatus bedStatus);

    List<Bed> findAllByRoomAndStatus(Room room, BedStatus bedstatus);

    @Modifying
    @Query(value = "DELETE FROM Bed WHERE hospital_id = :hospitalId AND room_name = :roomName AND status = 'Vacant' LIMIT :x;", nativeQuery = true)
    void deleteXVacantBedsInRoom(@Param("hospitalId") Integer hospitalId,@Param("roomName") String roomName,@Param("x") int x);

    void deleteAllByRoom(Room room);
}
