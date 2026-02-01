package com.hotel.models;

import javafx.beans.property.*;

public class Room {
    private final StringProperty roomNumber = new SimpleStringProperty();
    private final StringProperty type = new SimpleStringProperty();
    private final DoubleProperty price = new SimpleDoubleProperty();
    private final BooleanProperty available = new SimpleBooleanProperty();
    private final IntegerProperty capacity = new SimpleIntegerProperty();

    // Constructor
    public Room(String roomNumber, String type, double price, int capacity) {
        this.roomNumber.set(roomNumber);
        this.type.set(type);
        this.price.set(price);
        this.available.set(true);
        this.capacity.set(capacity);
    }

    // Property getters (for JavaFX binding)
    public StringProperty roomNumberProperty() { return roomNumber; }
    public StringProperty typeProperty() { return type; }
    public DoubleProperty priceProperty() { return price; }
    public BooleanProperty availableProperty() { return available; }
    public IntegerProperty capacityProperty() { return capacity; }

    // Regular getters and setters (for compatibility)
    public String getRoomNumber() { return roomNumber.get(); }
    public void setRoomNumber(String roomNumber) { this.roomNumber.set(roomNumber); }

    public String getType() { return type.get(); }
    public void setType(String type) { this.type.set(type); }

    public double getPrice() { return price.get(); }
    public void setPrice(double price) { this.price.set(price); }

    public boolean isAvailable() { return available.get(); }
    public void setAvailable(boolean available) { this.available.set(available); }

    public int getCapacity() { return capacity.get(); }
    public void setCapacity(int capacity) { this.capacity.set(capacity); }

    // Business methods remain the same
    public double calculatePrice(int numberOfNights) {
        return price.get() * numberOfNights;
    }

    @Override
    public String toString() {
        return "Room " + roomNumber.get() + " (" + type.get() + ") - $" + price.get() + "/night";
    }
}