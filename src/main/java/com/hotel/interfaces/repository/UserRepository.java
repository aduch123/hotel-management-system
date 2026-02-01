package com.hotel.interfaces.repository;

import com.hotel.models.User;

public interface UserRepository extends CrudRepository<User, String> {
    // User-specific operations
    User findByUsername(String username);
    boolean validateCredentials(String username, String password);
}