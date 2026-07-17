package com.flight.booking.service;

import com.flight.booking.entity.Booking;
import com.flight.booking.entity.Flist;
import com.flight.booking.entity.User;
import com.flight.booking.entity.BookHistory;
import com.flight.booking.repository.BookingRepository;
import com.flight.booking.repository.FlistRepository;
import com.flight.booking.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class BookingService {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private FlistRepository flistRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookHistoryService bookHistoryService;

    @Transactional
    public Booking bookFlight(Long userId, Long flightId, String passengerName, String passengerEmail) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found!"));
        Flist flight = flistRepository.findById(flightId)
                .orElseThrow(() -> new RuntimeException("Flight not found!"));

        if (flight.getAvailableSeats() <= 0) {
            throw new RuntimeException("No available seats left on this flight!");
        }

        // Decrement seats
        flight.setAvailableSeats(flight.getAvailableSeats() - 1);
        flistRepository.save(flight);

        // Assign seat number (e.g. FlightNo + SeatIndex)
        int bookedCount = flight.getTotalSeats() - flight.getAvailableSeats();
        String seatNumber = flight.getFlightNumber() + "-" + bookedCount;

        Booking booking = Booking.builder()
                .user(user)
                .flight(flight)
                .passengerName(passengerName)
                .passengerEmail(passengerEmail)
                .seatNumber(seatNumber)
                .bookingDate(LocalDateTime.now())
                .status("CONFIRMED")
                .build();

        Booking savedBooking = bookingRepository.save(booking);

        // Create log history entry
        bookHistoryService.logAction(
                savedBooking.getId(),
                user.getEmail(),
                flight.getFlightNumber(),
                "CREATE",
                "Successfully booked ticket for " + passengerName + " (Seat: " + seatNumber + ")"
        );

        return savedBooking;
    }

    @Transactional
    public Booking cancelBooking(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found!"));

        if ("CANCELLED".equals(booking.getStatus())) {
            throw new RuntimeException("Booking is already cancelled!");
        }

        // Increment seats
        Flist flight = booking.getFlight();
        flight.setAvailableSeats(flight.getAvailableSeats() + 1);
        flistRepository.save(flight);

        booking.setStatus("CANCELLED");
        Booking savedBooking = bookingRepository.save(booking);

        // Create log history entry
        bookHistoryService.logAction(
                booking.getId(),
                booking.getUser().getEmail(),
                flight.getFlightNumber(),
                "CANCEL",
                "Booking cancelled for " + booking.getPassengerName()
        );

        return savedBooking;
    }

    public List<Booking> getAllBookings() {
        return bookingRepository.findAll();
    }

    public Optional<Booking> getBookingById(Long id) {
        return bookingRepository.findById(id);
    }

    // Java Stream manipulation
    public List<Booking> getBookingsByUser(Long userId) {
        return bookingRepository.findAll().stream()
                .filter(b -> b.getUser().getId().equals(userId))
                .sorted(Comparator.comparing(Booking::getBookingDate).reversed())
                .collect(Collectors.toList());
    }

    // Java Stream analytics
    public double getTotalRevenue() {
        return bookingRepository.findAll().stream()
                .filter(b -> "CONFIRMED".equalsIgnoreCase(b.getStatus()))
                .mapToDouble(b -> b.getFlight().getPrice())
                .sum();
    }

    public Map<String, Object> getBookingStats() {
        List<Booking> bookings = bookingRepository.findAll();
        Map<String, Object> stats = new HashMap<>();

        long totalBookings = bookings.stream().count();
        long confirmedBookings = bookings.stream().filter(b -> "CONFIRMED".equalsIgnoreCase(b.getStatus())).count();
        long cancelledBookings = bookings.stream().filter(b -> "CANCELLED".equalsIgnoreCase(b.getStatus())).count();

        stats.put("totalBookings", totalBookings);
        stats.put("confirmedBookings", confirmedBookings);
        stats.put("cancelledBookings", cancelledBookings);
        stats.put("totalRevenue", getTotalRevenue());

        return stats;
    }
}
