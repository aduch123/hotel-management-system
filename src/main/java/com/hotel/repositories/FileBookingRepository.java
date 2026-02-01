package com.hotel.repositories;

import com.hotel.interfaces.repository.BookingRepository;
import com.hotel.models.Booking;
import com.hotel.models.Room;
import com.hotel.exceptions.DataAccessException;
import com.hotel.utils.DataManager;
import java.io.*;
import java.time.LocalDate;
import java.util.*;

public class FileBookingRepository implements BookingRepository {
//    private static final String DATA_DIR = "data";
//    private static final String FILE_PATH = DATA_DIR + File.separator + "bookings.txt";
    private DataManager data = new DataManager("bookings.txt");
    private final Map<String, Booking> bookingsCache;

    public FileBookingRepository() throws DataAccessException {
        this.bookingsCache = new HashMap<>();
        data.ensureDataDirectoryExists();
        loadFromFile();
    }

//    private void ensureDataDirectoryExists() {
//        File dataDir = new File(data.getDataDir());
//        if (!dataDir.exists()) {
//            dataDir.mkdirs();
//        }
//    }


    private void loadFromFile() throws DataAccessException {
        File file = new File(data.getFILE_PATH());

        if (!file.exists()) {
            System.out.println("Bookings file not found. Starting with empty bookings.");
            return; // No initial bookings
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            bookingsCache.clear();
            String line;
            while ((line = reader.readLine()) != null) {
                Booking booking = parseBookingLine(line);
                if (booking != null) {
                    bookingsCache.put(booking.getBookingId(), booking);
                }
            }
            System.out.println("Loaded " + bookingsCache.size() + " bookings from file.");

        } catch (IOException e) {
            throw new DataAccessException("Failed to load bookings from file: " + data.getFILE_PATH(), e);
        }
    }

    private void saveToFile() throws DataAccessException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(data.getFILE_PATH()))) {
            for (Booking booking : bookingsCache.values()) {
                writer.write(bookingToLine(booking));
                writer.newLine();
            }
            System.out.println("Saved " + bookingsCache.size() + " bookings to: " + data.getFILE_PATH());
        } catch (IOException e) {
            throw new DataAccessException("Failed to save bookings to file: " + data.getFILE_PATH(), e);
        }
    }

    private Booking parseBookingLine(String line) {
        try {
            String[] parts = line.split(",");
            if (parts.length != 7) {
                System.err.println("Invalid booking line format: " + line);
                return null;
            }

            String bookingId = parts[0].trim();
            String roomNumber = parts[1].trim();
            String customerName = parts[2].trim();
            LocalDate checkIn = LocalDate.parse(parts[3].trim());
            LocalDate checkOut = LocalDate.parse(parts[4].trim());
            int numberOfGuests = Integer.parseInt(parts[5].trim());
            String status = parts[6].trim();

            // Create a dummy room (will be replaced by actual room from repository)
            Room dummyRoom = new Room(roomNumber, "UNKNOWN", 0.0, numberOfGuests);

            Booking booking = new Booking(bookingId, dummyRoom, customerName, checkIn, checkOut, numberOfGuests);
            booking.setStatus(status);
            return booking;

        } catch (Exception e) {
            System.err.println("Error parsing booking line: " + line);
            return null;
        }
    }

    private String bookingToLine(Booking booking) {
        return String.join(",",
                booking.getBookingId(),
                booking.getRoom().getRoomNumber(),
                booking.getCustomerName(),
                booking.getCheckInDate().toString(),  // ISO format: YYYY-MM-DD
                booking.getCheckOutDate().toString(),
                String.valueOf(booking.getNumberOfGuests()),
                booking.getStatus()
        );
    }

    @Override
    public Booking save(Booking booking) {
        bookingsCache.put(booking.getBookingId(), booking);
        try {
            saveToFile();
            System.out.println("Saved booking: " + booking.getBookingId());
        } catch (DataAccessException e) {
            System.err.println("Warning: Failed to save booking to file: " + e.getMessage());
        }
        return booking;
    }

    @Override
    public Booking findById(String bookingId) {
        return bookingsCache.get(bookingId);
    }

    @Override
    public List<Booking> findAll() {
        return new ArrayList<>(bookingsCache.values());
    }

    @Override
    public void delete(String bookingId) {
        if (bookingsCache.remove(bookingId) != null) {
            try {
                saveToFile();
                System.out.println("Deleted booking: " + bookingId);
            } catch (DataAccessException e) {
                System.err.println("Warning: Failed to delete booking from file: " + e.getMessage());
            }
        }
    }

    @Override
    public boolean existsById(String bookingId) {
        return bookingsCache.containsKey(bookingId);
    }

    @Override
    public List<Booking> findByCustomerName(String customerName) {
        List<Booking> customerBookings = new ArrayList<>();
        for (Booking booking : bookingsCache.values()) {
            if (booking.getCustomerName().equalsIgnoreCase(customerName)) {
                customerBookings.add(booking);
            }
        }
        return customerBookings;
    }

    @Override
    public List<Booking> findByDateRange(LocalDate start, LocalDate end) {
        List<Booking> rangeBookings = new ArrayList<>();
        for (Booking booking : bookingsCache.values()) {
            LocalDate checkIn = booking.getCheckInDate();
            LocalDate checkOut = booking.getCheckOutDate();

            // Check if booking overlaps with the date range
            if (!checkOut.isBefore(start) && !checkIn.isAfter(end)) {
                rangeBookings.add(booking);
            }
        }
        return rangeBookings;
    }

    @Override
    public List<Booking> findCurrentBookings() {
        LocalDate today = LocalDate.now();
        List<Booking> currentBookings = new ArrayList<>();
        for (Booking booking : bookingsCache.values()) {
            if (!booking.getCheckInDate().isAfter(today) &&
                    !booking.getCheckOutDate().isBefore(today) &&
                    !"CANCELLED".equals(booking.getStatus()) &&
                    !"CHECKED_OUT".equals(booking.getStatus())) {
                currentBookings.add(booking);
            }
        }
        return currentBookings;
    }

    @Override
    public List<Booking> findByStatus(String status) {
        List<Booking> statusBookings = new ArrayList<>();
        for (Booking booking : bookingsCache.values()) {
            if (booking.getStatus().equalsIgnoreCase(status)) {
                statusBookings.add(booking);
            }
        }
        return statusBookings;
    }
}
