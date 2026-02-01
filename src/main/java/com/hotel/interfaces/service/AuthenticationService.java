package com.hotel.interfaces.service;

import com.hotel.models.User;

public interface AuthenticationService {
    // Authentication operations
    User login(String username, String password);
    void logout();
    User getCurrentUser();
    boolean isAuthenticated();

    // Authorization operations
    boolean hasPermission(String permission);
    boolean isManager();
    boolean isReceptionist();
    boolean isAccountant();
}