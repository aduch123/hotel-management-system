package com.hotel.interfaces.repository;

import com.hotel.models.Booking;
import java.time.LocalDate;
import java.util.List;

public interface BookingRepository extends CrudRepository<Booking, String> {
    List<Booking> findByCustomerName(String customerName);
    List<Booking> findByDateRange(LocalDate start, LocalDate end);
    List<Booking> findCurrentBookings();
    List<Booking> findByStatus(String status);
    List<Booking> findAll();
}