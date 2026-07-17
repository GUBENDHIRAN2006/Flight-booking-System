package com.flight.booking.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileUpdateDTO {
    private String name;
    private String email;
    private String password;
    private String passport;
    private String visa;
}
