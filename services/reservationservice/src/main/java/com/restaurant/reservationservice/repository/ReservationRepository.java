package com.restaurant.reservationservice.repository;
import com.restaurant.reservationservice.model.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    List<Reservation> findByCustomerEmail(String email);
    List<Reservation> findByReservationDate(LocalDate date);
    List<Reservation> findByReservationDateAndStatus(LocalDate date, Reservation.ReservationStatus status);
}
