package com.flight.booking.repository;

import com.flight.booking.entity.Flist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FlistRepository extends JpaRepository<Flist, Long> {
}
