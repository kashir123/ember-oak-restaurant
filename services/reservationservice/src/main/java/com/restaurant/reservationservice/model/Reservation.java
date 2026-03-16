package com.restaurant.reservationservice.model;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity @Table(name = "reservations")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class Reservation {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    private String customerName;
    private String customerEmail;
    private String customerPhone;
    private LocalDate reservationDate;
    private String reservationTime;
    private Integer partySize;
    private String specialRequests;
    @Enumerated(EnumType.STRING) private ReservationStatus status;
    private LocalDateTime createdAt;
    @PrePersist protected void onCreate() { createdAt = LocalDateTime.now(); if(status==null) status=ReservationStatus.CONFIRMED; }

    public enum ReservationStatus { CONFIRMED, CANCELLED, COMPLETED, NO_SHOW }
}
