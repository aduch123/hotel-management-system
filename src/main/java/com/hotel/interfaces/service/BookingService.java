package com.hotel.interfaces.service;

import com.hotel.models.Booking;
import java.time.LocalDate;
import java.util.List;

public interface BookingService {
    // Core booking operations
    Booking createBooking(String roomNumber, String customerName,
                          LocalDate checkIn, LocalDate checkOut,
                          int numberOfGuests);

    Booking confirmBooking(String bookingId);
    Booking cancelBooking(String bookingId);
    Booking checkIn(String bookingId);
    Booking checkOut(String bookingId);

    // Query operations
    Booking findBookingById(String bookingId);
    List<Booking> findAllBookings();  // Should match repository
    List<Booking> findBookingsByCustomer(String customerName);

    // Validation
    boolean isRoomAvailable(String roomNumber, LocalDate checkIn, LocalDate checkOut);
    double calculateBookingAmount(String roomNumber, int numberOfNights);
}