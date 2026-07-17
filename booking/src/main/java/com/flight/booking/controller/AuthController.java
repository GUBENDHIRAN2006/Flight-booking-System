package com.flight.booking.controller;

import com.flight.booking.dto.request.LoginReqDTO;
import com.flight.booking.dto.request.RegisterReqDTO;
import com.flight.booking.dto.response.ApiResponse;
import com.flight.booking.entity.User;
import com.flight.booking.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse> register(@RequestBody RegisterReqDTO dto) {
        try {
            User user = userService.register(dto);
            return ResponseEntity.ok(ApiResponse.success("Registration successful!", user));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse> login(@RequestBody LoginReqDTO dto, HttpSession session) {
        try {
            User user = userService.login(dto);
            session.setAttribute("user", user);
            return ResponseEntity.ok(ApiResponse.success("Login successful!", user));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse> logout(HttpSession session) {
        session.invalidate();
        return ResponseEntity.ok(ApiResponse.success("Logout successful!"));
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse> getCurrentUser(HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return ResponseEntity.status(401).body(ApiResponse.error("Not authenticated"));
        }
        return ResponseEntity.ok(ApiResponse.success("Authenticated user retrieved", user));
    }

    @PutMapping("/profile")
    public ResponseEntity<ApiResponse> updateProfile(@RequestBody com.flight.booking.dto.request.UserProfileUpdateDTO dto, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return ResponseEntity.status(401).body(ApiResponse.error("Not authenticated"));
        }
        try {
            User updated = userService.updateProfile(user.getId(), dto);
            session.setAttribute("user", updated);
            return ResponseEntity.ok(ApiResponse.success("Profile updated successfully", updated));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
}
