package com.hotel.interfaces.repository;

import com.hotel.models.Room;
import java.util.List;

public interface RoomRepository extends CrudRepository<Room, String> {
    List<Room> findAvailableRooms();
    List<Room> findByType(String type);
    List<Room> findByCapacity(int minCapacity);
    List<Room> findAll();  // Override to return List instead of Iterable
}