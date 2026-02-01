package com.hotel.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import java.io.File;
import java.io.IOException;

public class ManagerDashboardController {
    @FXML private Button roomsButton;
    @FXML private Button bookingsButton;
    @FXML private Button staffButton;
    @FXML private Button reportsButton;
    @FXML private Button logoutButton;
    @FXML private Label statusLabel;

    @FXML
    public void initialize() {
        // Setup button actions
        roomsButton.setOnAction(event -> handleRooms());
        bookingsButton.setOnAction(event -> handleBookings());
//        staffButton.setOnAction(event -> handleStaff());
//        reportsButton.setOnAction(event -> handleReports());
        logoutButton.setOnAction(event -> handleLogout());

        updateStatus();
    }

    private void handleRooms() {
        try {
            // Open room management screen
            File roomsFile = new File("src/main/resources/fxml/rooms_manager.fxml");
            FXMLLoader loader = new FXMLLoader(roomsFile.toURI().toURL());
            Parent root = loader.load();

            Stage roomStage = new Stage();
            roomStage.setTitle("Room Management");
            roomStage.setScene(new Scene(root, 900, 600));
            roomStage.show();

        } catch (IOException e) {
            showError("Failed to open room management: " + e.getMessage());
        }
    }

    private void handleBookings() {
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
    }

    private void handleStaff() {
        showMessage("Staff management - Add/remove staff, assign roles");
    }

    private void handleReports() {
        showMessage("Financial reports - Revenue, occupancy, performance");
    }

    private void handleLogout() {
        try {
            // Close dashboard
            Stage currentStage = (Stage) logoutButton.getScene().getWindow();
            currentStage.close();

            // Reload login screen
            File loginFile = new File("src/main/resources/fxml/login.fxml");
            FXMLLoader loader = new FXMLLoader(loginFile.toURI().toURL());
            Parent root = loader.load();

            Stage loginStage = new Stage();
            loginStage.setTitle("Hotel Management System - Login");
            loginStage.setScene(new Scene(root, 800, 600));
            loginStage.show();

        } catch (IOException e) {
            showError("Failed to logout: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void updateStatus() {
        statusLabel.setText("Manager Dashboard | " + java.time.LocalDateTime.now());
    }

    private void showMessage(String message) {
        statusLabel.setText(message);
        statusLabel.setStyle("-fx-text-fill: #2ecc71; -fx-font-weight: bold;");
    }

    private void showError(String message) {
        statusLabel.setText("Error: " + message);
        statusLabel.setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold;");
    }
}