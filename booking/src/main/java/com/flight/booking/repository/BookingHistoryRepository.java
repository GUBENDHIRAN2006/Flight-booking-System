package com.flight.booking.repository;

import com.flight.booking.entity.BookHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface BookingHistoryRepository extends JpaRepository<BookHistory, Long> {
    List<BookHistory> findByUserEmail(String userEmail);
}
