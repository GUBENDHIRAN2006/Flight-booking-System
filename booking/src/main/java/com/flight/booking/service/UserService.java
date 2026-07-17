package com.flight.booking.service;

import com.flight.booking.dto.request.LoginReqDTO;
import com.flight.booking.dto.request.RegisterReqDTO;
import com.flight.booking.entity.User;
import com.flight.booking.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public User register(RegisterReqDTO dto) {
        if (userRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new RuntimeException("Email is already registered!");
        }

        String role = "USER";
        if (dto.getEmail().toLowerCase().contains("admin")) {
            role = "ADMIN";
        }

        User user = User.builder()
                .name(dto.getName())
                .email(dto.getEmail())
                .password(dto.getPassword()) // Plain text for simplicity, or we can hash it
                .role(role)
                .build();

        return userRepository.save(user);
    }

    public User login(LoginReqDTO dto) {
        User user = userRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found!"));

        if (!user.getPassword().equals(dto.getPassword())) {
            throw new RuntimeException("Invalid credentials!");
        }

        return user;
    }

    public User updateProfile(Long userId, com.flight.booking.dto.request.UserProfileUpdateDTO dto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found!"));

        // Check if email changed and is already taken by another user
        if (!user.getEmail().equalsIgnoreCase(dto.getEmail())) {
            if (userRepository.findByEmail(dto.getEmail()).isPresent()) {
                throw new RuntimeException("Email is already taken!");
            }
        }

        user.setName(dto.getName());
        user.setEmail(dto.getEmail());
        if (dto.getPassword() != null && !dto.getPassword().trim().isEmpty()) {
            user.setPassword(dto.getPassword());
        }
        user.setPassport(dto.getPassport());
        user.setVisa(dto.getVisa());

        if ("admin@gmail.com".equalsIgnoreCase(dto.getEmail())) {
            user.setRole("ADMIN");
        }

        return userRepository.save(user);
    }
}
