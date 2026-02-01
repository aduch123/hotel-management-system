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

public class AccountantDashboardController {
    @FXML private Button paymentsButton;
    @FXML private Button invoicesButton;
    @FXML private Button reportsButton;
    @FXML private Button revenueButton;
    @FXML private Button logoutButton;
    @FXML private Label statusLabel;

    @FXML
    public void initialize() {
        // Setup button actions
        paymentsButton.setOnAction(event -> handlePayments());
        invoicesButton.setOnAction(event -> handleInvoices());
        reportsButton.setOnAction(event -> handleReports());
        revenueButton.setOnAction(event -> handleRevenue());
        logoutButton.setOnAction(event -> handleLogout());

        updateStatus();
    }

    private void handlePayments() {
        showMessage("Payment processing - Handle guest payments and refunds");
    }

    private void handleInvoices() {
        showMessage("Invoice management - Generate and manage invoices");
    }

    private void handleReports() {
        showMessage("Financial reports - Balance sheets, income statements");
    }

    private void handleRevenue() {
        showMessage("Revenue analysis - Daily, weekly, monthly revenue tracking");
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
        statusLabel.setText("Accountant Dashboard | " + java.time.LocalDateTime.now());
    }

    private void showMessage(String message) {
        statusLabel.setText(message);
        statusLabel.setStyle("-fx-text-fill: #9b59b6; -fx-font-weight: bold;");
    }

    private void showError(String message) {
        statusLabel.setText("Error: " + message);
        statusLabel.setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold;");
    }
}
