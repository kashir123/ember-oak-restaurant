package com.restaurant.reservationservice.service;
import com.restaurant.reservationservice.model.Reservation;
import com.restaurant.reservationservice.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.*;

@Service @RequiredArgsConstructor
public class ReservationService {
    private final ReservationRepository repo;
    private static final int MAX_PER_SLOT = 3;

    @Autowired(required = false)
    private EmailNotificationService emailService;

    public Reservation book(Reservation res) {
        Reservation saved = repo.save(res);
        if (emailService != null) emailService.sendReservationConfirmation(saved);
        return saved;
    }
    public List<Reservation> getAll()        { return repo.findAll(); }
    public Reservation getById(Long id)      { return repo.findById(id).orElseThrow(() -> new RuntimeException("Reservation not found")); }
    public List<Reservation> getByEmail(String email) { return repo.findByCustomerEmail(email); }
    public Reservation cancel(Long id) {
        Reservation r = getById(id); r.setStatus(Reservation.ReservationStatus.CANCELLED);
        Reservation saved = repo.save(r);
        if (emailService != null) emailService.sendReservationCancellation(saved);
        return saved;
    }

    /** Returns list of unavailable time slots for a given date */
    public List<String> getUnavailableSlots(LocalDate date, int guests) {
        List<Reservation> existing = repo.findByReservationDateAndStatus(date, Reservation.ReservationStatus.CONFIRMED);
        Map<String, Integer> slotCount = new HashMap<>();
        existing.forEach(r -> slotCount.merge(r.getReservationTime(), 1, Integer::sum));
        List<String> unavailable = new ArrayList<>();
        slotCount.forEach((slot, count) -> { if (count >= MAX_PER_SLOT) unavailable.add(slot); });
        return unavailable;
    }
}
