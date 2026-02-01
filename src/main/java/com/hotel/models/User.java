package com.hotel.models;

public abstract class User {
    private String username;
    private String password;
    //private String role; // "RECEPTIONIST", "MANAGER", "ACCOUNTANT"

    // Constructor
    public User(String username, String password) {
        this.username = username;
        this.password = password;
        //this.role = role;
    }

    // Getters and Setters
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public abstract String getRole();
//
//    public void setRole(String role) {
//        this.role = role;
//    }

    // Business method - checks if user has permission for an action
    public boolean hasPermission(String action) {return true;};

//    @Override
//    public String toString() {
//        return username + " (" + role + ")";
//    }
}
