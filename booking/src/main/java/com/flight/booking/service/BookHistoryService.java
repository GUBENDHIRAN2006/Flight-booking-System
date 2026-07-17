package com.flight.booking.service;

import com.flight.booking.entity.BookHistory;
import com.flight.booking.repository.BookingHistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BookHistoryService {

    @Autowired
    private BookingHistoryRepository bookingHistoryRepository;

    public void logAction(Long bookingId, String userEmail, String flightNumber, String action, String details) {
        BookHistory log = BookHistory.builder()
                .bookingId(bookingId)
                .userEmail(userEmail)
                .flightNumber(flightNumber)
                .action(action)
                .actionDate(LocalDateTime.now())
                .details(details)
                .build();
        bookingHistoryRepository.save(log);
    }

    // Java Stream manipulation - Sort logs descending
    public List<BookHistory> getAllHistory() {
        return bookingHistoryRepository.findAll().stream()
                .sorted(Comparator.comparing(BookHistory::getActionDate).reversed())
                .collect(Collectors.toList());
    }

    // Java Stream filtering
    public List<BookHistory> getHistoryByUserEmail(String userEmail) {
        return bookingHistoryRepository.findAll().stream()
                .filter(h -> h.getUserEmail().equalsIgnoreCase(userEmail.trim()))
                .sorted(Comparator.comparing(BookHistory::getActionDate).reversed())
                .collect(Collectors.toList());
    }
}
