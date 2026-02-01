package com.hotel.services;

import com.hotel.interfaces.repository.UserRepository;
import com.hotel.interfaces.service.AuthenticationService;
import com.hotel.models.User;
import com.hotel.exceptions.DataAccessException;

public class AuthenticationServiceImpl implements AuthenticationService {
    private final UserRepository userRepository;
    private User currentUser;

    public AuthenticationServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
        this.currentUser = null;
    }

    @Override
    public User login(String username, String password) {
        // Validate credentials
        boolean isValid = userRepository.validateCredentials(username, password);

        if (!isValid) {
            System.err.println("Invalid username or password");
            return null;
        }

        // Get user details
        currentUser = userRepository.findByUsername(username);
        System.out.println("Login successful: " + currentUser.getUsername() + " (" + currentUser.getRole() + ")");

        return currentUser;
    }

    @Override
    public void logout() {
        if (currentUser != null) {
            System.out.println("Logging out: " + currentUser.getUsername());
            currentUser = null;
        }
    }

    @Override
    public User getCurrentUser() {
        return currentUser;
    }

    @Override
    public boolean isAuthenticated() {
        return currentUser != null;
    }

    @Override
    public boolean hasPermission(String permission) {
        if (currentUser == null) {
            return false;
        }
        return currentUser.hasPermission(permission);
    }

    @Override
    public boolean isManager() {
        return currentUser != null && "MANAGER".equals(currentUser.getRole());
    }

    @Override
    public boolean isReceptionist() {
        return currentUser != null && "RECEPTIONIST".equals(currentUser.getRole());
    }

    @Override
    public boolean isAccountant() {
        return currentUser != null && "ACCOUNTANT".equals(currentUser.getRole());
    }
}
