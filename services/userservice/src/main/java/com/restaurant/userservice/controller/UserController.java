package com.restaurant.userservice.controller;

import com.restaurant.userservice.model.*;
import com.restaurant.userservice.service.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class UserController {

    private final UserService userService;
    private final JwtService jwtService;

    /** POST /api/users/register */
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest req) {
        return ResponseEntity.ok(userService.register(req));
    }

    /** POST /api/users/login */
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody AuthRequest req) {
        return ResponseEntity.ok(userService.login(req));
    }

    /** GET /api/users/profile  (requires Bearer token) */
    @GetMapping("/profile")
    public ResponseEntity<?> profile(@RequestHeader("Authorization") String auth) {
        String token = auth.replace("Bearer ", "");
        if (!jwtService.isValid(token)) return ResponseEntity.status(401).body("Invalid token");
        String email = jwtService.extractEmail(token);
        return ResponseEntity.ok(userService.getProfile(email));
    }

    /** GET /api/users/health */
    @GetMapping("/health")
    public ResponseEntity<String> health() { return ResponseEntity.ok("User Service UP"); }
}
