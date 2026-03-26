package com.restaurant.orderservice.service;
import com.restaurant.orderservice.model.*;
import com.restaurant.orderservice.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.List;

@Service @RequiredArgsConstructor
public class OrderService {
    private final OrderRepository repo;

    @Autowired(required = false)
    private EmailNotificationService emailService;

    public Order placeOrder(Order order) {
        if (order.getItems() != null)
            order.getItems().forEach(i -> i.setOrder(order));
        BigDecimal total = order.getItems() == null ? BigDecimal.ZERO :
            order.getItems().stream().map(OrderItem::getSubtotal).reduce(BigDecimal.ZERO, BigDecimal::add);
        order.setTotalAmount(total);
        Order saved = repo.save(order);
        if (emailService != null) emailService.sendOrderConfirmation(saved);
        return saved;
    }
    public List<Order> getAll()                     { return repo.findAll(); }
    public Order getById(Long id)                   { return repo.findById(id).orElseThrow(() -> new RuntimeException("Order not found")); }
    public List<Order> getByEmail(String email)     { return repo.findByCustomerEmail(email); }
    public Order cancel(Long id) {
        Order o = getById(id);
        o.setStatus(Order.OrderStatus.CANCELLED);
        Order saved = repo.save(o);
        if (emailService != null) emailService.sendOrderCancellation(saved);
        return saved;
    }
    public Order updateStatus(Long id, Order.OrderStatus status) {
        Order o = getById(id); o.setStatus(status); return repo.save(o);
    }
}
