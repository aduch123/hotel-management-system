package com.hotel.models;

import javafx.beans.property.*;
import java.time.LocalDate;

public class Booking {
    private final StringProperty bookingId = new SimpleStringProperty();
    private final StringProperty roomNumber = new SimpleStringProperty();
    private final StringProperty customerName = new SimpleStringProperty();
    private final ObjectProperty<LocalDate> checkInDate = new SimpleObjectProperty<>();
    private final ObjectProperty<LocalDate> checkOutDate = new SimpleObjectProperty<>();
    private final IntegerProperty numberOfGuests = new SimpleIntegerProperty();
    private final StringProperty status = new SimpleStringProperty();

    // Reference to actual Room object (for business logic)
    private Room room;

    // Constructor
    public Booking(String bookingId, Room room, String customerName,
                   LocalDate checkInDate, LocalDate checkOutDate, int numberOfGuests) {
        this.bookingId.set(bookingId);
        this.room = room;
        this.roomNumber.set(room.getRoomNumber());
        this.customerName.set(customerName);
        this.checkInDate.set(checkInDate);
        this.checkOutDate.set(checkOutDate);
        this.numberOfGuests.set(numberOfGuests);
        this.status.set("PENDING");
    }

    // Property getters (for JavaFX binding)
    public StringProperty bookingIdProperty() { return bookingId; }
    public StringProperty roomNumberProperty() { return roomNumber; }
    public StringProperty customerNameProperty() { return customerName; }
    public ObjectProperty<LocalDate> checkInDateProperty() { return checkInDate; }
    public ObjectProperty<LocalDate> checkOutDateProperty() { return checkOutDate; }
    public IntegerProperty numberOfGuestsProperty() { return numberOfGuests; }
    public StringProperty statusProperty() { return status; }

    // Regular getters and setters
    public String getBookingId() { return bookingId.get(); }
    public void setBookingId(String bookingId) { this.bookingId.set(bookingId); }

    public String getRoomNumber() { return roomNumber.get(); }
    public void setRoomNumber(String roomNumber) { this.roomNumber.set(roomNumber); }

    public String getCustomerName() { return customerName.get(); }
    public void setCustomerName(String customerName) { this.customerName.set(customerName); }

    public LocalDate getCheckInDate() { return checkInDate.get(); }
    public void setCheckInDate(LocalDate checkInDate) { this.checkInDate.set(checkInDate); }

    public LocalDate getCheckOutDate() { return checkOutDate.get(); }
    public void setCheckOutDate(LocalDate checkOutDate) { this.checkOutDate.set(checkOutDate); }

    public int getNumberOfGuests() { return numberOfGuests.get(); }
    public void setNumberOfGuests(int numberOfGuests) { this.numberOfGuests.set(numberOfGuests); }

    public String getStatus() { return status.get(); }
    public void setStatus(String status) { this.status.set(status); }

    public Room getRoom() { return room; }
    public void setRoom(Room room) {
        this.room = room;
        this.roomNumber.set(room.getRoomNumber());
    }

    // Business methods (unchanged)
    public int getNumberOfNights() {
        return (int) java.time.temporal.ChronoUnit.DAYS.between(checkInDate.get(), checkOutDate.get());
    }

    public double calculateTotalAmount() {
        return room.calculatePrice(getNumberOfNights());
    }

    public boolean isValid() {
        return checkInDate.get().isBefore(checkOutDate.get()) &&
                numberOfGuests.get() <= room.getCapacity() &&
                room.isAvailable();
    }

    @Override
    public String toString() {
        return "Booking #" + bookingId.get() + " - " + customerName.get() + " in Room " + roomNumber.get();
    }
}
