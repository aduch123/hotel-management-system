package com.hotel.interfaces.service;

import com.hotel.models.Room;
import java.util.List;

public interface RoomService {
    // Room management operations
    Room addRoom(String roomNumber, String type, double price, int capacity);
    Room updateRoom(String roomNumber, double newPrice);
    boolean removeRoom(String roomNumber);

    // Query operations
    Room findRoomByNumber(String roomNumber);
    List<Room> getAllRooms();  // Should match repository
    List<Room> getAvailableRooms();
    List<Room> getRoomsByType(String type);

    // Room status operations
    boolean setRoomAvailability(String roomNumber, boolean available);
    boolean isRoomAvailable(String roomNumber);
}