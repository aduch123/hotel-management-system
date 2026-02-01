package com.hotel.controllers;

import com.hotel.interfaces.repository.BookingRepository;
import com.hotel.interfaces.repository.RoomRepository;
import com.hotel.models.Booking;
import com.hotel.models.Room;
import com.hotel.repositories.FileBookingRepository;
import com.hotel.repositories.FileRoomRepository;
import com.hotel.services.BookingServiceImpl;
import com.hotel.services.RoomServiceImpl;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;

public class addRoomController {
    @FXML private TextField roomNumberField;
    @FXML private ComboBox<String> typeComboBox;
    @FXML private TextField priceField;
    @FXML private TextField capacityField;
    @FXML private Button addRoom;
    @FXML private Button backButton;
    @FXML private Label messageLabel;

    @FXML
    public void initialize() {
        // Setup button actions
        addRoom.setOnAction(event -> handleAddRoom());

        typeComboBox.getItems().addAll("STANDARD", "DELUXE", "SUITE");
        typeComboBox.setValue("STANDARD");

    }

    public void handleAddRoom() {
        String roomNumber = roomNumberField.getText().trim();
        String type = typeComboBox.getValue();

        priceField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.matches("\\d*(\\.\\d{0,2})?")) {
                priceField.setText(oldVal);
            }
        });
        double price = Double.parseDouble(priceField.getText().trim());

        capacityField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.matches("\\d*")) {
                capacityField.setText(oldVal);
            }
        });
        int capacity = Integer.parseInt(capacityField.getText());

        if (roomNumber.isEmpty()) {
            showError("All fields must be given!");
            return;
        }

        try {
            RoomRepository roomRepo = new FileRoomRepository();

            Room newRoom = new RoomServiceImpl(roomRepo).addRoom(roomNumber, type, price, capacity);
            if (newRoom == null) {
                showError("Room already exists.");
                return;
            }
            Stage currentStage = (Stage) backButton.getScene().getWindow();
            currentStage.close();
            try {
                File roomsFile = new File("src/main/resources/fxml/rooms_manager.fxml");
                FXMLLoader loader = new FXMLLoader(roomsFile.toURI().toURL());
                Parent root = loader.load();

                Stage roomStage = new Stage();
                roomStage.setTitle("Room Management");
                roomStage.setScene(new Scene(root, 1100, 700));
                roomStage.show();

            } catch (IOException e) {
                showError("Failed to open room management: " + e.getMessage());
            }
        } catch (Exception e) {
            showError("Login error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void handleBack() {
        Stage currentStage = (Stage) backButton.getScene().getWindow();
        currentStage.close();
    }


    private void showError(String message) {
        messageLabel.setText(message);
        messageLabel.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
    }
}
