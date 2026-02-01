package com.hotel.services;

import com.hotel.interfaces.repository.BookingRepository;
import com.hotel.interfaces.repository.RoomRepository;
import com.hotel.interfaces.service.BookingService;
import com.hotel.models.Booking;
import com.hotel.models.Room;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final RoomRepository roomRepository;

    public BookingServiceImpl(BookingRepository bookingRepository, RoomRepository roomRepository) {
        this.bookingRepository = bookingRepository;
        this.roomRepository = roomRepository;
    }

    @Override
    public Booking createBooking(String roomNumber, String customerName,
                                 LocalDate checkIn, LocalDate checkOut,
                                 int numberOfGuests) {

        // Validate dates
        if (checkIn.isBefore(LocalDate.now())) {
            System.err.println("Check-in date cannot be in the past!");
            return null;
        }

        if (!checkIn.isBefore(checkOut)) {
            System.err.println("Check-out date must be after check-in date!");
            return null;
        }

        // Get room
        Room room = roomRepository.findById(roomNumber);
        if (room == null) {
            System.err.println("Room " + roomNumber + " not found!");
            return null;
        }

        // Check room availability
        if (!room.isAvailable()) {
            System.err.println("Room " + roomNumber + " is not available!");
            return null;
        }

        // Check capacity
        if (numberOfGuests > room.getCapacity()) {
            System.err.println("Number of guests exceeds room capacity!");
            return null;
        }

        // Check if room is already booked for these dates
        List<Booking> existingBookings = bookingRepository.findByDateRange(checkIn, checkOut);
        for (Booking existing : existingBookings) {
            if (existing.getRoom().getRoomNumber().equals(roomNumber) &&
                    !"CANCELLED".equals(existing.getStatus())) {
                System.err.println("Room " + roomNumber + " is already booked for these dates!");
                return null;
            }
        }

        // Generate booking ID
        String bookingId = "BK" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        // Create booking
        Booking booking = new Booking(bookingId, room, customerName, checkIn, checkOut, numberOfGuests);
        booking.setStatus("PENDING");

        // Save booking
        booking = bookingRepository.save(booking);

        // Update room availability
        room.setAvailable(false);
        roomRepository.save(room);

        System.out.println("Booking created: " + bookingId + " for " + customerName);
        return booking;
    }

    @Override
    public Booking confirmBooking(String bookingId) {
        Booking booking = bookingRepository.findById(bookingId);

        if (booking == null) {
            System.err.println("Booking " + bookingId + " not found!");
            return null;
        }

        if (!"PENDING".equals(booking.getStatus())) {
            System.err.println("Booking is not in PENDING status!");
            return null;
        }

        booking.setStatus("CONFIRMED");
        return bookingRepository.save(booking);
    }

    @Override
    public Booking cancelBooking(String bookingId) {
        Booking booking = bookingRepository.findById(bookingId);

        if (booking == null) {
            System.err.println("Booking " + bookingId + " not found!");
            return null;
        }

        if ("CHECKED_OUT".equals(booking.getStatus()) || "CANCELLED".equals(booking.getStatus())) {
            System.err.println("Cannot cancel booking in current status!");
            return null;
        }

        // Update room availability
        Room room = booking.getRoom();
        room.setAvailable(true);
        roomRepository.save(room);

        booking.setStatus("CANCELLED");
        return bookingRepository.save(booking);
    }

    @Override
    public Booking checkIn(String bookingId) {
        Booking booking = bookingRepository.findById(bookingId);

        if (booking == null) {
            System.err.println("Booking " + bookingId + " not found!");
            return null;
        }

        if (!"CONFIRMED".equals(booking.getStatus())) {
            System.err.println("Booking must be CONFIRMED before check-in!");
            return null;
        }

        booking.setStatus("CHECKED_IN");
        return bookingRepository.save(booking);
    }

    @Override
    public Booking checkOut(String bookingId) {
        Booking booking = bookingRepository.findById(bookingId);

        if (booking == null) {
            System.err.println("Booking " + bookingId + " not found!");
            return null;
        }

        if (!"CHECKED_IN".equals(booking.getStatus())) {
            System.err.println("Booking must be CHECKED_IN before check-out!");
            return null;
        }

        // Update room availability
        Room room = booking.getRoom();
        room.setAvailable(true);
        roomRepository.save(room);

        booking.setStatus("CHECKED_OUT");
        return bookingRepository.save(booking);
    }

    @Override
    public Booking findBookingById(String bookingId) {
        return bookingRepository.findById(bookingId);
    }

    @Override
    public List<Booking> findAllBookings() {
        return bookingRepository.findAll();
    }

    @Override
    public List<Booking> findBookingsByCustomer(String customerName) {
        return bookingRepository.findByCustomerName(customerName);
    }

    @Override
    public boolean isRoomAvailable(String roomNumber, LocalDate checkIn, LocalDate checkOut) {
        Room room = roomRepository.findById(roomNumber);
        if (room == null || !room.isAvailable()) {
            return false;
        }

        // Check for overlapping bookings
        List<Booking> overlappingBookings = bookingRepository.findByDateRange(checkIn, checkOut);
        for (Booking booking : overlappingBookings) {
            if (booking.getRoom().getRoomNumber().equals(roomNumber) &&
                    !"CANCELLED".equals(booking.getStatus())) {
                return false;
            }
        }

        return true;
    }

    @Override
    public double calculateBookingAmount(String roomNumber, int numberOfNights) {
        Room room = roomRepository.findById(roomNumber);
        if (room == null) {
            return 0.0;
        }
        return room.calculatePrice(numberOfNights);
    }
}