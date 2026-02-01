package com.hotel.controllers;

import com.hotel.interfaces.repository.BookingRepository;
import com.hotel.interfaces.repository.RoomRepository;
import com.hotel.interfaces.repository.UserRepository;
import com.hotel.models.Booking;
import com.hotel.models.User;
import com.hotel.repositories.FileBookingRepository;
import com.hotel.repositories.FileRoomRepository;
import com.hotel.repositories.FileUserRepository;
import com.hotel.services.AuthenticationServiceImpl;
import com.hotel.Main;
import com.hotel.services.BookingServiceImpl;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import java.io.IOException;
import java.io.File;
import java.time.LocalDate;

public class newBookingController {
    @FXML private TextField roomNumberField;
    @FXML private TextField customerNameField;
    @FXML private DatePicker fromDatePicker;
    @FXML private DatePicker toDatePicker;
    @FXML private Button createButton;
    @FXML private Button backButton;
    @FXML private Label messageLabel;

    @FXML
    public void initialize() {
        // Setup button actions
        createButton.setOnAction(event -> handleCreate());
        backButton.setOnAction(event -> handleBack());

    }

    public void handleCreate() {
        String roomNumber = roomNumberField.getText().trim();
        String customerName = customerNameField.getText().trim();
        LocalDate fromDate = fromDatePicker.getValue();
        LocalDate toDate = toDatePicker.getValue();

        if (roomNumber.isEmpty() || customerName.isEmpty()) {
            showError("All fields must be given!");
            return;
        }

        try {
            RoomRepository roomRepo = new FileRoomRepository();
            BookingRepository bookingRepo = new FileBookingRepository();

            Booking newBooking = new BookingServiceImpl(bookingRepo, roomRepo).createBooking(roomNumber, customerName, fromDate, toDate, 2);
            if (newBooking == null) {
                showError("Couldn't create booking");
                return;
            }
            Stage currentStage = (Stage) backButton.getScene().getWindow();
            currentStage.close();
            try {
                File bookingsFile = new File("src/main/resources/fxml/booking.fxml");
                FXMLLoader loader = new FXMLLoader(bookingsFile.toURI().toURL());
                Parent root = loader.load();

                Stage bookingStage = new Stage();
                bookingStage.setTitle("Booking Management");
                bookingStage.setScene(new Scene(root, 1100, 700));
                bookingStage.show();

            } catch (IOException e) {
                showError("Failed to open booking management: " + e.getMessage());
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
