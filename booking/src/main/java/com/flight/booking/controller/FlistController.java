package com.flight.booking.controller;

import com.flight.booking.dto.response.ApiResponse;
import com.flight.booking.entity.Flist;
import com.flight.booking.service.FlistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/flights")
public class FlistController {

    @Autowired
    private FlistService flistService;

    @GetMapping
    public ResponseEntity<ApiResponse> getFlights(
            @RequestParam(required = false) String source,
            @RequestParam(required = false) String destination,
            @RequestParam(required = false) String date,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(required = false) String airline) {
        
        // Search and filter using streams inside service
        List<Flist> flights = flistService.searchFlights(source, destination, date, maxPrice, airline);
        return ResponseEntity.ok(ApiResponse.success("Flights retrieved successfully", flights));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> getFlightById(@PathVariable Long id) {
        return flistService.getFlightById(id)
                .map(flight -> ResponseEntity.ok(ApiResponse.success("Flight retrieved", flight)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<ApiResponse> createFlight(@RequestBody Flist flight) {
        try {
            Flist saved = flistService.saveFlight(flight);
            return ResponseEntity.ok(ApiResponse.success("Flight created successfully", saved));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse> updateFlight(@PathVariable Long id, @RequestBody Flist flightDetails) {
        try {
            Flist updated = flistService.updateFlight(id, flightDetails);
            return ResponseEntity.ok(ApiResponse.success("Flight updated successfully", updated));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteFlight(@PathVariable Long id) {
        try {
            flistService.deleteFlight(id);
            return ResponseEntity.ok(ApiResponse.success("Flight deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/stats")
    public ResponseEntity<ApiResponse> getFlightStats() {
        try {
            Map<String, Object> stats = flistService.getFlightStats();
            return ResponseEntity.ok(ApiResponse.success("Flight statistics retrieved", stats));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
}
