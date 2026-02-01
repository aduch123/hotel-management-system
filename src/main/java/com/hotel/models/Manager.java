package com.hotel.models;

public class Manager extends User{
    private String role = "MANAGER";

    public Manager(String username, String password) {
        super(username, password);
    }

    public String getRole() {return this.role;}

    @Override
    public String toString() {
        return getUsername() + "MANAGER";
    }
}
