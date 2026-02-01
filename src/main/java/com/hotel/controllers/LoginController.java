package com.hotel.controllers;

import com.hotel.exceptions.DataAccessException;
import com.hotel.interfaces.repository.UserRepository;
import com.hotel.models.User;
import com.hotel.repositories.FileUserRepository;
import com.hotel.services.AuthenticationServiceImpl;
import com.hotel.Main;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import java.io.IOException;
import java.io.File;

public class LoginController {
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private ComboBox<String> roleComboBox;
    @FXML private Button loginButton;
    @FXML private Button exitButton;
    @FXML private Label messageLabel;

    private AuthenticationServiceImpl authService;

    // This method is called by JavaFX after FXML is loaded
    @FXML
    public void initialize() {
        // Setup role options
        roleComboBox.getItems().addAll("RECEPTIONIST", "MANAGER", "ACCOUNTANT");
        roleComboBox.setValue("RECEPTIONIST");

        // Setup button actions
        loginButton.setOnAction(event -> handleLogin());
        exitButton.setOnAction(event -> handleExit());

        // Initialize auth service (in real app, this would be injected)
        // For now, we'll create it when dashboard opens
    }

    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();
        String role = roleComboBox.getValue();

        // Basic validation
        if (username.isEmpty() || password.isEmpty()) {
            showError("Please enter both username and password");
            return;
        }

        try {
            // For demo purposes, we'll use hardcoded credentials
            // In the integrated version, this will use the real authService
            if (isValidCredentials(username, password, role)) {
                showSuccess("Login successful! Loading dashboard...");
                loadDashboard(role);
            } else {
                showError("Invalid credentials for " + role + " role");
            }

        } catch (Exception e) {
            showError("Login error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private boolean isValidCredentials(String username, String password, String role) {
        // Hardcoded credentials for demo
        // This will be replaced with real authentication later
//        switch (role) {
//            case "RECEPTIONIST":
//                return username.equals("reception") && password.equals("pass123");
//            case "MANAGER":
//                return username.equals("manager") && password.equals("admin123");
//            case "ACCOUNTANT":
//                return username.equals("accountant") && password.equals("finance123");
//            default:
//                return false;
//        }

        try {
            UserRepository userRepo = new FileUserRepository();
            User user = new AuthenticationServiceImpl(userRepo).login(username, password);

            return user.getRole().equals(role);
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private void loadDashboard(String role) {
        try {
            // Close login window
            Stage currentStage = (Stage) loginButton.getScene().getWindow();
            currentStage.close();

            // Determine which dashboard to load based on role
            String dashboardFileName = getDashboardFileName(role);
            File dashboardFile = new File("src/main/resources/fxml/" + dashboardFileName);

            System.out.println("Loading dashboard: " + dashboardFile.getAbsolutePath());

            if (!dashboardFile.exists()) {
                throw new IOException("Dashboard file not found: " + dashboardFile.getAbsolutePath());
            }

            // Load dashboard
            FXMLLoader loader = new FXMLLoader(dashboardFile.toURI().toURL());
            Parent root = loader.load();

            Stage dashboardStage = new Stage();
            dashboardStage.setTitle("Hotel Dashboard - " + role);
            dashboardStage.setScene(new Scene(root, 1000, 700));
            dashboardStage.show();

        } catch (IOException e) {
            showError("Failed to load dashboard: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private String getDashboardFileName(String role) {
        switch (role) {
            case "RECEPTIONIST":
                return "receptionist_dashboard.fxml";
            case "MANAGER":
                return "manager_dashboard.fxml";
            case "ACCOUNTANT":
                return "accountant_dashboard.fxml";
            default:
                return "receptionist_dashboard.fxml";
        }
    }

    private String getDashboardForRole(String role) {
        switch (role) {
            case "RECEPTIONIST":
                return "/fxml/receptionist_dashboard.fxml";
            case "MANAGER":
                return "/fxml/manager_dashboard.fxml";
            case "ACCOUNTANT":
                return "/fxml/accountant_dashboard.fxml";
            default:
                return "/fxml/receptionist_dashboard.fxml";
        }
    }

    private void handleExit() {
        Stage stage = (Stage) exitButton.getScene().getWindow();
        stage.close();
    }

    private void showError(String message) {
        messageLabel.setText(message);
        messageLabel.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
    }

    private void showSuccess(String message) {
        messageLabel.setText(message);
        messageLabel.setStyle("-fx-text-fill: green; -fx-font-weight: bold;");
    }
}
