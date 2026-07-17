package com.flight.booking.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "booking_history")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long bookingId;
    private String userEmail;
    private String flightNumber;
    private String action; // "CREATE", "CANCEL"
    private LocalDateTime actionDate;
    private String details;
}
