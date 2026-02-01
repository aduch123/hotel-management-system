package com.hotel.models;

public class Receptionist extends User {
    private String role = "RECEPTIONIST";

    public Receptionist(String username, String password) {
        super(username, password);
    }

    public String getRole() {return this.role;}

    @Override
    public boolean hasPermission(String action) {
        return action.equals("BOOKING") || action.equals("CHECKIN") || action.equals("CHECKOUT");
    }

    @Override
    public String toString() {
        return getUsername() + "RECEPTIONIST";
    }
}
