package com.hotel.repositories;

import com.hotel.interfaces.repository.UserRepository;
import com.hotel.models.*;
import com.hotel.exceptions.DataAccessException;
import com.hotel.utils.DataManager;

import java.io.*;
import java.util.*;

public class FileUserRepository implements UserRepository {
//    private static final String DATA_DIR = "data";
//    private static final String FILE_PATH = DATA_DIR + File.separator + "users.txt";
    private DataManager data = new DataManager("user.txt");
    private final Map<String, User> usersCache;

    public FileUserRepository() throws DataAccessException {
        this.usersCache = new HashMap<>();
        data.ensureDataDirectoryExists();
        loadFromFile();
    }

//    private void ensureDataDirectoryExists() {
//        File dataDir = new File(data.getDataDir());
//        if (!dataDir.exists()) {
//            dataDir.mkdirs();
//        }
//    }

    private void loadFromFile() throws DataAccessException {
        File file = new File(data.getFILE_PATH());

        if (!file.exists()) {
            System.out.println("Users file not found. Creating default users...");
            initializeDefaultUsers();
            saveToFile();
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            usersCache.clear();
            String line;
            while ((line = reader.readLine()) != null) {
                User user = parseUserLine(line);
                if (user != null) {
                    usersCache.put(user.getUsername(), user);
                }
            }
            System.out.println("Loaded " + usersCache.size() + " users from file.");

        } catch (IOException e) {
            throw new DataAccessException("Failed to load users from file: " + data.getFILE_PATH(), e);
        }
    }

    private void saveToFile() throws DataAccessException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(data.getFILE_PATH()))) {
            for (User user : usersCache.values()) {
                writer.write(userToLine(user));
                writer.newLine();
            }
            System.out.println("Saved " + usersCache.size() + " users to: " + data.getFILE_PATH());
        } catch (IOException e) {
            throw new DataAccessException("Failed to save users to file: " + data.getFILE_PATH(), e);
        }
    }

    private User parseUserLine(String line) {
        try {
            String[] parts = line.split(",");
            if (parts.length != 3) {
                System.err.println("Invalid user line format: " + line);
                return null;
            }

            String username = parts[0].trim();
            String password = parts[1].trim();
            String role = parts[2].trim();

            if(role.equals("MANAGER")) {
                return new Manager(username, password);
            }
            return new Receptionist(username, password);

        } catch (Exception e) {
            System.err.println("Error parsing user line: " + line);
            return null;
        }
    }

    private String userToLine(User user) {
        return String.join(",",
                user.getUsername(),
                user.getPassword(),
                user.getRole()
        );
    }

    private void initializeDefaultUsers() {
        // Create default users for testing
        usersCache.put("reception", new Receptionist("reception", "pass123"));
        usersCache.put("manager", new Manager("manager", "admin123"));

        System.out.println("Initialized " + usersCache.size() + " default users.");
    }

    @Override
    public User save(User user) {
        usersCache.put(user.getUsername(), user);
        try {
            saveToFile();
            System.out.println("Saved user: " + user.getUsername());
        } catch (DataAccessException e) {
            System.err.println("Warning: Failed to save user to file: " + e.getMessage());
        }
        return user;
    }

    @Override
    public User findById(String username) {
        return usersCache.get(username);
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(usersCache.values());
    }

    @Override
    public void delete(String username) {
        if (usersCache.remove(username) != null) {
            try {
                saveToFile();
                System.out.println("Deleted user: " + username);
            } catch (DataAccessException e) {
                System.err.println("Warning: Failed to delete user from file: " + e.getMessage());
            }
        }
    }

    @Override
    public boolean existsById(String username) {
        return usersCache.containsKey(username);
    }

    @Override
    public User findByUsername(String username) {
        return usersCache.get(username);
    }

    @Override
    public boolean validateCredentials(String username, String password) {
        User user = usersCache.get(username);
        if (user == null) {
            return false;
        }
        return user.getPassword().equals(password);
    }
}
