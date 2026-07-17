package com.flight.booking.controller;

import com.flight.booking.dto.response.ApiResponse;
import com.flight.booking.service.BookHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/history")
public class BookHistory {

    @Autowired
    private BookHistoryService bookHistoryService;

    @GetMapping
    public ResponseEntity<ApiResponse> getAllHistory() {
        return ResponseEntity.ok(ApiResponse.success("All action logs retrieved", bookHistoryService.getAllHistory()));
    }

    @GetMapping("/user/{email}")
    public ResponseEntity<ApiResponse> getHistoryByUser(@PathVariable String email) {
        return ResponseEntity.ok(ApiResponse.success("User history retrieved", bookHistoryService.getHistoryByUserEmail(email)));
    }
}
