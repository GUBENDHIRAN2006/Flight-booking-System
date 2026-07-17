package com.flight.booking.service;

import com.flight.booking.entity.Flist;
import com.flight.booking.repository.FlistRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class FlistService {

    @Autowired
    private FlistRepository flistRepository;

    public List<Flist> getAllFlights() {
        return flistRepository.findAll();
    }

    public Optional<Flist> getFlightById(Long id) {
        return flistRepository.findById(id);
    }

    public Flist saveFlight(Flist flight) {
        return flistRepository.save(flight);
    }

    public Flist updateFlight(Long id, Flist flightDetails) {
        Flist flight = flistRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Flight not found!"));
        
        flight.setFlightNumber(flightDetails.getFlightNumber());
        flight.setAirline(flightDetails.getAirline());
        flight.setSource(flightDetails.getSource());
        flight.setDestination(flightDetails.getDestination());
        flight.setDepartureTime(flightDetails.getDepartureTime());
        flight.setArrivalTime(flightDetails.getArrivalTime());
        flight.setPrice(flightDetails.getPrice());
        flight.setAvailableSeats(flightDetails.getAvailableSeats());
        flight.setTotalSeats(flightDetails.getTotalSeats());
        flight.setFlightType(flightDetails.getFlightType());
        flight.setImageUrl(flightDetails.getImageUrl());
        
        return flistRepository.save(flight);
    }

    public void deleteFlight(Long id) {
        flistRepository.deleteById(id);
    }

    // Java Stream based filtering/searching
    public List<Flist> searchFlights(String source, String destination, String dateStr, Double maxPrice, String airline) {
        return flistRepository.findAll().stream()
                .filter(f -> source == null || source.trim().isEmpty() || f.getSource().equalsIgnoreCase(source.trim()))
                .filter(f -> destination == null || destination.trim().isEmpty() || f.getDestination().equalsIgnoreCase(destination.trim()))
                .filter(f -> {
                    if (dateStr == null || dateStr.trim().isEmpty()) return true;
                    // match date in YYYY-MM-DD
                    return f.getDepartureTime().toLocalDate().toString().equals(dateStr.trim());
                })
                .filter(f -> maxPrice == null || f.getPrice() <= maxPrice)
                .filter(f -> airline == null || airline.trim().isEmpty() || f.getAirline().equalsIgnoreCase(airline.trim()))
                .collect(Collectors.toList());
    }

    // Java Stream based analytics/statistics
    public Map<String, Object> getFlightStats() {
        List<Flist> flights = flistRepository.findAll();
        Map<String, Object> stats = new HashMap<>();

        long totalFlights = flights.stream().count();
        double avgPrice = flights.stream().mapToDouble(Flist::getPrice).average().orElse(0.0);
        double maxPrice = flights.stream().mapToDouble(Flist::getPrice).max().orElse(0.0);
        double minPrice = flights.stream().mapToDouble(Flist::getPrice).min().orElse(0.0);

        // Group flights by airline using stream mapping
        Map<String, Long> flightsByAirline = flights.stream()
                .collect(Collectors.groupingBy(Flist::getAirline, Collectors.counting()));

        // Cheapest 3 flights
        List<Flist> cheapestFlights = flights.stream()
                .sorted(Comparator.comparingDouble(Flist::getPrice))
                .limit(3)
                .collect(Collectors.toList());

        stats.put("totalFlights", totalFlights);
        stats.put("averagePrice", avgPrice);
        stats.put("maxPrice", maxPrice);
        stats.put("minPrice", minPrice);
        stats.put("flightsByAirline", flightsByAirline);
        stats.put("cheapestFlights", cheapestFlights);

        return stats;
    }
}
