package com.restaurant.orderservice.model;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity @Table(name = "orders")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class Order {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    private String customerName;
    private String customerEmail;
    private String customerPhone;
    @Enumerated(EnumType.STRING) private OrderType orderType;
    @Enumerated(EnumType.STRING) private OrderStatus status;
    private String deliveryAddress;
    private Integer tableNumber;
    private String notes;
    private BigDecimal totalAmount;
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<OrderItem> items;
    private LocalDateTime createdAt;
    @PrePersist protected void onCreate() { createdAt = LocalDateTime.now(); if(status==null) status=OrderStatus.PENDING; }

    public enum OrderType   { DINE_IN, TAKEOUT, DELIVERY }
    public enum OrderStatus { PENDING, CONFIRMED, PREPARING, READY, DELIVERED, CANCELLED }
}
