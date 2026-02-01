package com.hotel.services;

import com.hotel.interfaces.repository.RoomRepository;
import com.hotel.interfaces.service.RoomService;
import com.hotel.models.Room;
import com.hotel.exceptions.DataAccessException;
import java.util.List;

public class RoomServiceImpl implements RoomService {
    private final RoomRepository roomRepository;

    public RoomServiceImpl(RoomRepository roomRepository) {
        this.roomRepository = roomRepository;
    }

    @Override
    public Room addRoom(String roomNumber, String type, double price, int capacity) {
        // Check if room already exists
        if (roomRepository.existsById(roomNumber)) {
            System.err.println("Room " + roomNumber + " already exists!");
            return null;
        }

        // Create and save new room
        Room newRoom = new Room(roomNumber, type, price, capacity);
        return roomRepository.save(newRoom);
    }

    @Override
    public Room updateRoom(String roomNumber, double newPrice) {
        Room room = roomRepository.findById(roomNumber);
        if (room == null) {
            System.err.println("Room " + roomNumber + " not found!");
            return null;
        }

        room.setPrice(newPrice);
        return roomRepository.save(room);
    }

    @Override
    public boolean removeRoom(String roomNumber) {
        if (!roomRepository.existsById(roomNumber)) {
            System.err.println("Room " + roomNumber + " not found!");
            return false;
        }

        roomRepository.delete(roomNumber);
        return true;
    }

    @Override
    public Room findRoomByNumber(String roomNumber) {
        return roomRepository.findById(roomNumber);
    }

    @Override
    public List<Room> getAllRooms() {
        return roomRepository.findAll();
    }

    @Override
    public List<Room> getAvailableRooms() {
        return roomRepository.findAvailableRooms();
    }

    @Override
    public List<Room> getRoomsByType(String type) {
        return roomRepository.findByType(type);
    }

    @Override
    public boolean setRoomAvailability(String roomNumber, boolean available) {
        Room room = roomRepository.findById(roomNumber);
        if (room == null) {
            System.err.println("Room " + roomNumber + " not found!");
            return false;
        }

        room.setAvailable(available);
        roomRepository.save(room);
        return true;
    }

    @Override
    public boolean isRoomAvailable(String roomNumber) {
        Room room = roomRepository.findById(roomNumber);
        return room != null && room.isAvailable();
    }
}
