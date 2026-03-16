package com.restaurant.userservice.model;

import lombok.*;

@Data @AllArgsConstructor
public class AuthResponse {
    private String token;
    private String email;
    private String firstName;
    private String lastName;
    private String role;
}
