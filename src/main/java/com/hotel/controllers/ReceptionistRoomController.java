package com.hotel.controllers;

import com.hotel.models.Room;
import com.hotel.services.RoomServiceImpl;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.util.List;

public class ReceptionistRoomController {
    @FXML private TableView<Room> roomsTable;
    @FXML private TableColumn<Room, String> roomNumberColumn;
    @FXML private TableColumn<Room, String> typeColumn;
    @FXML private TableColumn<Room, Double> priceColumn;
    @FXML private TableColumn<Room, Boolean> availableColumn;
    @FXML private TableColumn<Room, Integer> capacityColumn;

    @FXML private TextField searchField;
    @FXML private ComboBox<String> typeFilter;
    @FXML private Button refreshButton;
    @FXML private Button backButton;
    @FXML private Label statusLabel;

    private RoomServiceImpl roomService;

    @FXML
    public void initialize() {
        // Initialize room service (in real app, this would be injected)
        // For now, we'll initialize it here
        initializeRoomService();

        // Setup table columns
        roomNumberColumn.setCellValueFactory(new PropertyValueFactory<>("roomNumber"));
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        availableColumn.setCellValueFactory(new PropertyValueFactory<>("available"));
        capacityColumn.setCellValueFactory(new PropertyValueFactory<>("capacity"));

        // Setup filter options
        typeFilter.getItems().addAll("ALL", "STANDARD", "DELUXE", "SUITE");
        typeFilter.setValue("ALL");

        //setup search and type filter actions
        searchField.textProperty().addListener(event -> loadRooms());
        typeFilter.setOnAction(event -> loadRooms());

        // Setup button actions
        refreshButton.setOnAction(event -> loadRooms());
        backButton.setOnAction(event -> handleBack());

        // Load initial data
        loadRooms();

        statusLabel.setText("Room Management - Loaded " + roomsTable.getItems().size() + " rooms");
    }

    private void initializeRoomService() {
        try {
            // In a real application, this would be injected
            // For now, we'll create a simple version
            com.hotel.repositories.FileRoomRepository roomRepo =
                    new com.hotel.repositories.FileRoomRepository();
            roomService = new RoomServiceImpl(roomRepo);
        } catch (Exception e) {
            showError("Failed to initialize room service: " + e.getMessage());
        }
    }

    private void loadRooms() {
        if (roomService == null) {
            showError("Room service not initialized");
            return;
        }

        try {
            List<Room> allRooms = roomService.getAllRooms();

            // Apply filters
            String searchText = searchField.getText().toLowerCase();
            String selectedType = typeFilter.getValue();

            List<Room> filteredRooms = allRooms.stream()
                    .filter(room ->
                            (searchText.isEmpty() ||
                                    room.getRoomNumber().toLowerCase().contains(searchText) ||
                                    room.getType().toLowerCase().contains(searchText)) &&
                                    (selectedType.equals("ALL") || room.getType().equals(selectedType))
                    )
                    .toList();

            roomsTable.getItems().setAll(filteredRooms);

            statusLabel.setText("Loaded " + filteredRooms.size() + " of " +
                    allRooms.size() + " rooms");

        } catch (Exception e) {
            showError("Failed to load rooms: " + e.getMessage());
        }
    }

    private void handleAddRoom() {
        showMessage("Add Room feature will be implemented here");
        // TODO: Open add room dialog
    }

    private void handleEditRoom() {
        Room selectedRoom = roomsTable.getSelectionModel().getSelectedItem();
        if (selectedRoom == null) {
            showError("Please select a room to edit");
            return;
        }

        showMessage("Editing room: " + selectedRoom.getRoomNumber());
        // TODO: Open edit dialog with selected room details
    }

    private void handleDeleteRoom() {
        Room selectedRoom = roomsTable.getSelectionModel().getSelectedItem();
        if (selectedRoom == null) {
            showError("Please select a room to delete");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Delete");
        alert.setHeaderText("Delete Room " + selectedRoom.getRoomNumber());
        alert.setContentText("Are you sure you want to delete this room?");

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                boolean success = roomService.removeRoom(selectedRoom.getRoomNumber());
                if (success) {
                    showMessage("Room " + selectedRoom.getRoomNumber() + " deleted successfully");
                    loadRooms();
                } else {
                    showError("Failed to delete room " + selectedRoom.getRoomNumber());
                }
            }
        });
    }

    private void handleBack() {
        //try {
            // Close current window
            Stage currentStage = (Stage) backButton.getScene().getWindow();
            currentStage.close();

//            // Go back to dashboard based on user role
//            // For now, go to receptionist dashboard
//            File dashboardFile = new File("src/main/resources/fxml/receptionist_dashboard.fxml");
//            FXMLLoader loader = new FXMLLoader(dashboardFile.toURI().toURL());
//            Parent root = loader.load();
//
//            Stage dashboardStage = new Stage();
//            dashboardStage.setTitle("Hotel Dashboard");
//            dashboardStage.setScene(new Scene(root, 1000, 700));
//            dashboardStage.show();

//        } catch (IOException e) {
//            showError("Failed to go back: " + e.getMessage());
//        }
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