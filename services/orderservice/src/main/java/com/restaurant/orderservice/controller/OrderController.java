package com.restaurant.orderservice.controller;
import com.restaurant.orderservice.model.Order;
import com.restaurant.orderservice.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController @RequestMapping("/api/orders")
@RequiredArgsConstructor @CrossOrigin(origins = "*")
public class OrderController {
    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<Order> place(@RequestBody Order order) {
        return ResponseEntity.status(HttpStatus.CREATED).body(orderService.placeOrder(order));
    }
    @GetMapping
    public ResponseEntity<List<Order>> getAll() { return ResponseEntity.ok(orderService.getAll()); }
    @GetMapping("/{id}")
    public ResponseEntity<Order> getById(@PathVariable Long id) { return ResponseEntity.ok(orderService.getById(id)); }
    @GetMapping("/my")
    public ResponseEntity<List<Order>> myOrders(@RequestParam String email) { return ResponseEntity.ok(orderService.getByEmail(email)); }
    @PutMapping("/{id}/cancel")
    public ResponseEntity<Order> cancel(@PathVariable Long id) { return ResponseEntity.ok(orderService.cancel(id)); }
    @PutMapping("/{id}/status")
    public ResponseEntity<Order> status(@PathVariable Long id, @RequestParam Order.OrderStatus status) {
        return ResponseEntity.ok(orderService.updateStatus(id, status));
    }
    @GetMapping("/health")
    public ResponseEntity<String> health() { return ResponseEntity.ok("Order Service UP"); }
}
