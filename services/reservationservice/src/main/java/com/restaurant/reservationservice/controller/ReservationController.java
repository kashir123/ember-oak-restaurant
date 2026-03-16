package com.restaurant.reservationservice.controller;
import com.restaurant.reservationservice.model.Reservation;
import com.restaurant.reservationservice.service.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.*;

@RestController @RequestMapping("/api/reservations")
@RequiredArgsConstructor @CrossOrigin(origins = "*")
public class ReservationController {
    private final ReservationService reservationService;

    @PostMapping
    public ResponseEntity<Reservation> book(@RequestBody Reservation res) {
        return ResponseEntity.status(HttpStatus.CREATED).body(reservationService.book(res));
    }
    @GetMapping
    public ResponseEntity<List<Reservation>> getAll() { return ResponseEntity.ok(reservationService.getAll()); }
    @GetMapping("/{id}")
    public ResponseEntity<Reservation> getById(@PathVariable Long id) { return ResponseEntity.ok(reservationService.getById(id)); }
    @GetMapping("/my")
    public ResponseEntity<List<Reservation>> myReservations(@RequestParam String email) { return ResponseEntity.ok(reservationService.getByEmail(email)); }
    @PutMapping("/{id}/cancel")
    public ResponseEntity<Reservation> cancel(@PathVariable Long id) { return ResponseEntity.ok(reservationService.cancel(id)); }
    @GetMapping("/availability")
    public ResponseEntity<Map<String,Object>> availability(
            @RequestParam String date,
            @RequestParam(defaultValue="2") int guests) {
        List<String> unavailable = reservationService.getUnavailableSlots(LocalDate.parse(date), guests);
        return ResponseEntity.ok(Map.of("unavailableSlots", unavailable, "date", date));
    }
    @GetMapping("/health")
    public ResponseEntity<String> health() { return ResponseEntity.ok("Reservation Service UP"); }
}
