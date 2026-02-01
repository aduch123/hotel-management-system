package com.hotel.controllers;

import com.hotel.services.BookingServiceImpl;
import com.hotel.services.RoomServiceImpl;
import com.hotel.models.Booking;
import com.hotel.models.Room;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

public class BookingController {
    @FXML private TableView<Booking> bookingsTable;
    @FXML private TableColumn<Booking, String> bookingIdColumn;
    @FXML private TableColumn<Booking, String> customerNameColumn;
    @FXML private TableColumn<Booking, String> roomNumberColumn;
    @FXML private TableColumn<Booking, LocalDate> checkInColumn;
    @FXML private TableColumn<Booking, LocalDate> checkOutColumn;
    @FXML private TableColumn<Booking, Integer> guestsColumn;
    @FXML private TableColumn<Booking, String> statusColumn;
    @FXML private TableColumn<Booking, String> nightsColumn;

    @FXML private TextField searchField;
    @FXML private ComboBox<String> statusFilter;
    @FXML private DatePicker fromDatePicker;
    @FXML private DatePicker toDatePicker;

    @FXML private Button refreshButton;
    @FXML private Button newBookingButton;
    @FXML private Button confirmButton;
    @FXML private Button cancelButton;
    @FXML private Button checkInButton;
    @FXML private Button checkOutButton;
    @FXML private Button backButton;
    @FXML private Label statusLabel;
    @FXML private Label summaryLabel;

    private BookingServiceImpl bookingService;
    private RoomServiceImpl roomService;

    @FXML
    public void initialize() {
        // Initialize services
        initializeServices();

        // Setup table columns
        bookingIdColumn.setCellValueFactory(new PropertyValueFactory<>("bookingId"));
        customerNameColumn.setCellValueFactory(new PropertyValueFactory<>("customerName"));
        roomNumberColumn.setCellValueFactory(new PropertyValueFactory<>("roomNumber"));
        checkInColumn.setCellValueFactory(new PropertyValueFactory<>("checkInDate"));
        checkOutColumn.setCellValueFactory(new PropertyValueFactory<>("checkOutDate"));
        guestsColumn.setCellValueFactory(new PropertyValueFactory<>("numberOfGuests"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        // Custom column for calculated nights
        nightsColumn.setCellValueFactory(cellData -> {
            Booking booking = cellData.getValue();
            int nights = booking.getNumberOfNights();
            return new javafx.beans.property.SimpleStringProperty(nights + " nights");
        });

        // Setup filter options
        statusFilter.getItems().addAll("ALL", "PENDING", "CONFIRMED", "CHECKED_IN", "CHECKED_OUT", "CANCELLED");
        statusFilter.setValue("ALL");

        //setup search, data pickers and type filter actions
        searchField.textProperty().addListener(event -> loadBookings());
        fromDatePicker.setOnAction(event -> loadBookings());
        statusFilter.setOnAction(event -> loadBookings());


        // Setup date pickers
        fromDatePicker.setValue(LocalDate.now().minusDays(7));
        toDatePicker.setValue(LocalDate.now().plusDays(30));

        // Setup button actions
        refreshButton.setOnAction(event -> loadBookings());
        newBookingButton.setOnAction(event -> handleNewBooking());
        confirmButton.setOnAction(event -> handleConfirmBooking());
        cancelButton.setOnAction(event -> handleCancelBooking());
        checkInButton.setOnAction(event -> handleCheckIn());
        checkOutButton.setOnAction(event -> handleCheckOut());
        backButton.setOnAction(event -> handleBack());

        // Load initial data
        loadBookings();

        updateButtonStates();
    }

    private void initializeServices() {
        try {
            // Initialize repositories and services
            com.hotel.repositories.FileRoomRepository roomRepo =
                    new com.hotel.repositories.FileRoomRepository();
            com.hotel.repositories.FileBookingRepository bookingRepo =
                    new com.hotel.repositories.FileBookingRepository();

            roomService = new RoomServiceImpl(roomRepo);
            bookingService = new BookingServiceImpl(bookingRepo, roomRepo);

        } catch (Exception e) {
            showError("Failed to initialize services: " + e.getMessage());
        }
    }

    private void loadBookings() {
        if (bookingService == null) {
            showError("Booking service not initialized");
            return;
        }

        try {
            List<Booking> allBookings = bookingService.findAllBookings();

            // Apply filters
            String searchText = searchField.getText().toLowerCase();
            String selectedStatus = statusFilter.getValue();
            LocalDate fromDate = fromDatePicker.getValue();
            LocalDate toDate = toDatePicker.getValue();

            List<Booking> filteredBookings = allBookings.stream()
                    .filter(booking ->
                            (searchText.isEmpty() ||
                                    booking.getCustomerName().toLowerCase().contains(searchText) ||
                                    booking.getRoomNumber().toLowerCase().contains(searchText) ||
                                    booking.getBookingId().toLowerCase().contains(searchText)) &&
                                    (selectedStatus.equals("ALL") || booking.getStatus().equals(selectedStatus)) &&
                                    (fromDate == null || !booking.getCheckInDate().isBefore(fromDate)) &&
                                    (toDate == null || !booking.getCheckInDate().isAfter(toDate))
                    )
                    .toList();

            bookingsTable.getItems().setAll(filteredBookings);

            // Update summary
            updateSummary(filteredBookings);

            statusLabel.setText("Loaded " + filteredBookings.size() + " of " +
                    allBookings.size() + " bookings");

        } catch (Exception e) {
            showError("Failed to load bookings: " + e.getMessage());
        }
    }

    private void updateSummary(List<Booking> bookings) {
        long pending = bookings.stream().filter(b -> b.getStatus().equals("PENDING")).count();
        long confirmed = bookings.stream().filter(b -> b.getStatus().equals("CONFIRMED")).count();
        long checkedIn = bookings.stream().filter(b -> b.getStatus().equals("CHECKED_IN")).count();
        long checkedOut = bookings.stream().filter(b -> b.getStatus().equals("CHECKED_OUT")).count();
        long cancelled = bookings.stream().filter(b -> b.getStatus().equals("CANCELLED")).count();

        double totalRevenue = bookings.stream()
                .filter(b -> b.getStatus().equals("CHECKED_OUT"))
                .mapToDouble(Booking::calculateTotalAmount)
                .sum();

        summaryLabel.setText(String.format(
                "Summary: %d bookings | Pending: %d | Confirmed: %d | Checked-in: %d | Checked-out: %d | Cancelled: %d | Revenue: $%.2f",
                bookings.size(), pending, confirmed, checkedIn, checkedOut, cancelled, totalRevenue
        ));
    }

    private void handleNewBooking() {
        try {
            Stage currentStage = (Stage) backButton.getScene().getWindow();
            currentStage.close();
            // Open new booking dialog
            File bookingDialogFile = new File("src/main/resources/fxml/new_booking.fxml");
            if (!bookingDialogFile.exists()) {
                // Create a simple dialog for now
                showSimpleNewBookingDialog();
            } else {
                FXMLLoader loader = new FXMLLoader(bookingDialogFile.toURI().toURL());
                Parent root = loader.load();

                Stage dialogStage = new Stage();
                dialogStage.setTitle("Create New Booking");
                dialogStage.setScene(new Scene(root, 500, 400));
                dialogStage.showAndWait();

                // Refresh after dialog closes
                loadBookings();
            }

        } catch (Exception e) {
            showError("Failed to create new booking: " + e.getMessage());
        }
    }

    private void showSimpleNewBookingDialog() {
        // Create a simple input dialog
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("New Booking");
        dialog.setHeaderText("Create a new booking");
        dialog.setContentText("Enter customer name:");

        dialog.showAndWait().ifPresent(customerName -> {
            // For simplicity, create a test booking
            try {
                // Find first available room
                List<Room> availableRooms = roomService.getAvailableRooms();
                if (!availableRooms.isEmpty()) {
                    Room room = availableRooms.get(0);
                    LocalDate checkIn = LocalDate.now().plusDays(1);
                    LocalDate checkOut = checkIn.plusDays(3);

                    Booking newBooking = bookingService.createBooking(
                            room.getRoomNumber(),
                            customerName,
                            checkIn,
                            checkOut,
                            2
                    );

                    if (newBooking != null) {
                        showMessage("Booking created: " + newBooking.getBookingId());
                        loadBookings();
                    } else {
                        showError("Failed to create booking");
                    }
                } else {
                    showError("No rooms available for booking");
                }
            } catch (Exception e) {
                showError("Error: " + e.getMessage());
            }
        });
    }

    private void handleConfirmBooking() {
        Booking selectedBooking = bookingsTable.getSelectionModel().getSelectedItem();
        if (selectedBooking == null) {
            showError("Please select a booking to confirm");
            return;
        }

        if (!"PENDING".equals(selectedBooking.getStatus())) {
            showError("Only PENDING bookings can be confirmed");
            return;
        }

        Booking confirmed = bookingService.confirmBooking(selectedBooking.getBookingId());
        if (confirmed != null) {
            showMessage("Booking " + selectedBooking.getBookingId() + " confirmed successfully");
            loadBookings();
            updateButtonStates();
        } else {
            showError("Failed to confirm booking");
        }
    }

    private void handleCancelBooking() {
        Booking selectedBooking = bookingsTable.getSelectionModel().getSelectedItem();
        if (selectedBooking == null) {
            showError("Please select a booking to cancel");
            return;
        }

        if ("CHECKED_OUT".equals(selectedBooking.getStatus()) ||
                "CANCELLED".equals(selectedBooking.getStatus())) {
            showError("Cannot cancel booking in current status");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Cancellation");
        alert.setHeaderText("Cancel Booking " + selectedBooking.getBookingId());
        alert.setContentText("Are you sure you want to cancel this booking?");

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                Booking cancelled = bookingService.cancelBooking(selectedBooking.getBookingId());
                if (cancelled != null) {
                    showMessage("Booking " + selectedBooking.getBookingId() + " cancelled successfully");
                    loadBookings();
                    updateButtonStates();
                } else {
                    showError("Failed to cancel booking");
                }
            }
        });
    }

    private void handleCheckIn() {
        Booking selectedBooking = bookingsTable.getSelectionModel().getSelectedItem();
        if (selectedBooking == null) {
            showError("Please select a booking for check-in");
            return;
        }

        if (!"CONFIRMED".equals(selectedBooking.getStatus())) {
            showError("Only CONFIRMED bookings can be checked in");
            return;
        }

        Booking checkedIn = bookingService.checkIn(selectedBooking.getBookingId());
        if (checkedIn != null) {
            showMessage("Booking " + selectedBooking.getBookingId() + " checked in successfully");
            loadBookings();
            updateButtonStates();
        } else {
            showError("Failed to check in booking");
        }
    }

    private void handleCheckOut() {
        Booking selectedBooking = bookingsTable.getSelectionModel().getSelectedItem();
        if (selectedBooking == null) {
            showError("Please select a booking for check-out");
            return;
        }

        if (!"CHECKED_IN".equals(selectedBooking.getStatus())) {
            showError("Only CHECKED_IN bookings can be checked out");
            return;
        }

        Booking checkedOut = bookingService.checkOut(selectedBooking.getBookingId());
        if (checkedOut != null) {
            // Calculate and show amount
            double amount = selectedBooking.calculateTotalAmount();
            showMessage("Booking " + selectedBooking.getBookingId() +
                    " checked out successfully. Amount: $" + amount);
            loadBookings();
            updateButtonStates();
        } else {
            showError("Failed to check out booking");
        }
    }

    private void updateButtonStates() {
        Booking selectedBooking = bookingsTable.getSelectionModel().getSelectedItem();

        if (selectedBooking == null) {
            confirmButton.setDisable(true);
            cancelButton.setDisable(true);
            checkInButton.setDisable(true);
            checkOutButton.setDisable(true);
            return;
        }

        String status = selectedBooking.getStatus();
        confirmButton.setDisable(!status.equals("PENDING"));
        cancelButton.setDisable(status.equals("CHECKED_OUT") || status.equals("CANCELLED"));
        checkInButton.setDisable(!status.equals("CONFIRMED"));
        checkOutButton.setDisable(!status.equals("CHECKED_IN"));
    }

    private void handleBack() {
//        try {
            Stage currentStage = (Stage) backButton.getScene().getWindow();
            currentStage.close();

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
