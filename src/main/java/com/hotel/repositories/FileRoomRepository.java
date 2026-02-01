package com.hotel.repositories;

import com.hotel.interfaces.repository.RoomRepository;
import com.hotel.models.Booking;
import com.hotel.models.Room;
import com.hotel.exceptions.DataAccessException;
import com.hotel.utils.DataManager;

import java.io.*;
import java.util.*;

public class FileRoomRepository implements RoomRepository {
//    private static final String DATA_DIR = "data";
//    private static final String FILE_PATH = DATA_DIR + File.separator + "rooms.txt";
    private DataManager data = new DataManager("rooms.txt");
    private final Map<String, Room> roomsCache;

    public FileRoomRepository() throws DataAccessException {
        this.roomsCache = new HashMap<>();
        data.ensureDataDirectoryExists();
        loadFromFile();
    }

//    private void ensureDataDirectoryExists() {
//        File dataDir = new File(data.getDataDir());
//        if (!dataDir.exists()) {
//            boolean created = dataDir.mkdirs();
//            if (!created) {
//                System.err.println("Warning: Could not create data directory");
//            }
//        }
//    }

    private void loadFromFile() throws DataAccessException {
        File file = new File(data.getFILE_PATH());

        if (!file.exists()) {
            System.out.println("Rooms file not found. Creating initial data...");
            initializeDefaultRooms();
            saveToFile();  // This should work now since directory exists
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            roomsCache.clear();
            String line;
            while ((line = reader.readLine()) != null) {
                Room room = parseRoomLine(line);
                if (room != null) {
                    roomsCache.put(room.getRoomNumber(), room);
                }
            }
            System.out.println("Loaded " + roomsCache.size() + " rooms from file.");

        } catch (IOException e) {
            throw new DataAccessException("Failed to load rooms from file: " + data.getFILE_PATH(), e);
        }
    }

    private void saveToFile() throws DataAccessException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(data.getFILE_PATH()))) {
            for (Room room : roomsCache.values()) {
                writer.write(roomToLine(room));
                writer.newLine();
            }
            System.out.println("Saved " + roomsCache.size() + " rooms to: " + data.getFILE_PATH());
        } catch (IOException e) {
            throw new DataAccessException("Failed to save rooms to file: " + data.getFILE_PATH(), e);
        }
    }

    private Room parseRoomLine(String line) {
        try {
            String[] parts = line.split(",");
            if (parts.length != 5) {
                System.err.println("Invalid line format: " + line);
                return null;
            }

            String roomNumber = parts[0].trim();
            String type = parts[1].trim();
            double price = Double.parseDouble(parts[2].trim());
            boolean available = Boolean.parseBoolean(parts[3].trim());
            int capacity = Integer.parseInt(parts[4].trim());

            Room room = new Room(roomNumber, type, price, capacity);
            room.setAvailable(available);
            return room;

        } catch (NumberFormatException e) {
            System.err.println("Error parsing numbers in line: " + line);
            return null;
        } catch (Exception e) {
            System.err.println("Error parsing room line: " + line);
            return null;
        }
    }

    private String roomToLine(Room room) {
        return String.join(",",
                room.getRoomNumber(),
                room.getType(),
                String.valueOf(room.getPrice()),
                String.valueOf(room.isAvailable()),
                String.valueOf(room.getCapacity())
        );
    }

    private void initializeDefaultRooms() {
        // Clear any existing data first
        roomsCache.clear();

        // Add default rooms
        roomsCache.put("101", new Room("101", "STANDARD", 100.0, 2));
        roomsCache.put("102", new Room("102", "STANDARD", 100.0, 2));
        roomsCache.put("201", new Room("201", "DELUXE", 150.0, 4));
        roomsCache.put("202", new Room("202", "DELUXE", 150.0, 4));
        roomsCache.put("301", new Room("301", "SUITE", 300.0, 6));
        roomsCache.put("302", new Room("302", "SUITE", 300.0, 6));

        System.out.println("Initialized " + roomsCache.size() + " default rooms.");
    }

    @Override
    public Room save(Room room) {
        roomsCache.put(room.getRoomNumber(), room);
        try {
            saveToFile();
            System.out.println("Saved room: " + room.getRoomNumber());
        } catch (DataAccessException e) {
            System.err.println("Warning: Failed to save room to file: " + e.getMessage());
        }
        return room;
    }

    @Override
    public Room findById(String roomNumber) {
        return roomsCache.get(roomNumber);
    }

    @Override
    public List<Room> findAll() {
        return new ArrayList<>(roomsCache.values());
    }

    @Override
    public void delete(String roomNumber) {
        if (roomsCache.remove(roomNumber) != null) {
            try {
                saveToFile();
                System.out.println("Deleted room: " + roomNumber);
            } catch (DataAccessException e) {
                System.err.println("Warning: Failed to delete room from file: " + e.getMessage());
            }
        }
    }

    @Override
    public boolean existsById(String roomNumber) {
        return roomsCache.containsKey(roomNumber);
    }

    @Override
    public List<Room> findAvailableRooms() {
        List<Room> availableRooms = new ArrayList<>();
        for (Room room : roomsCache.values()) {
            if (room.isAvailable()) {
                availableRooms.add(room);
            }
        }
        return availableRooms;
    }

    @Override
    public List<Room> findByType(String type) {
        List<Room> typedRooms = new ArrayList<>();
        for (Room room : roomsCache.values()) {
            if (room.getType().equalsIgnoreCase(type)) {
                typedRooms.add(room);
            }
        }
        return typedRooms;
    }

    @Override
    public List<Room> findByCapacity(int minCapacity) {
        List<Room> capacityRooms = new ArrayList<>();
        for (Room room : roomsCache.values()) {
            if (room.getCapacity() >= minCapacity) {
                capacityRooms.add(room);
            }
        }
        return capacityRooms;
    }
}