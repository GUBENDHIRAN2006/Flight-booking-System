package com.flight.booking.controller;

import com.flight.booking.dto.response.ApiResponse;
import com.flight.booking.entity.Booking;
import com.flight.booking.entity.User;
import com.flight.booking.service.BookingService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    @Autowired
    private BookingService bookingService;

    @PostMapping
    public ResponseEntity<ApiResponse> createBooking(@RequestBody Map<String, Object> payload, HttpSession session) {
        try {
            User user = (User) session.getAttribute("user");
            Long userId;
            if (user != null) {
                userId = user.getId();
            } else if (payload.containsKey("userId")) {
                userId = Long.valueOf(payload.get("userId").toString());
            } else {
                return ResponseEntity.status(401).body(ApiResponse.error("Please log in to make a booking."));
            }

            Long flightId = Long.valueOf(payload.get("flightId").toString());
            String passengerName = payload.get("passengerName").toString();
            String passengerEmail = payload.get("passengerEmail").toString();

            Booking booking = bookingService.bookFlight(userId, flightId, passengerName, passengerEmail);
            return ResponseEntity.ok(ApiResponse.success("Booking confirmed successfully!", booking));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> cancelBooking(@PathVariable Long id) {
        try {
            Booking booking = bookingService.cancelBooking(id);
            return ResponseEntity.ok(ApiResponse.success("Booking cancelled successfully!", booking));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping
    public ResponseEntity<ApiResponse> getAllBookings() {
        List<Booking> bookings = bookingService.getAllBookings();
        return ResponseEntity.ok(ApiResponse.success("All bookings retrieved", bookings));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse> getBookingsByUser(@PathVariable Long userId) {
        List<Booking> bookings = bookingService.getBookingsByUser(userId);
        return ResponseEntity.ok(ApiResponse.success("User bookings retrieved", bookings));
    }

    @GetMapping("/stats")
    public ResponseEntity<ApiResponse> getBookingStats() {
        try {
            Map<String, Object> stats = bookingService.getBookingStats();
            return ResponseEntity.ok(ApiResponse.success("Booking stats retrieved", stats));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
}
