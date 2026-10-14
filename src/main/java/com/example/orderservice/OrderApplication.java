package com.example.orderservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@SpringBootApplication
public class OrderApplication {

    public static void main(String[] args) {
        SpringApplication.run(OrderApplication.class, args);
    }
}

@RestController
@RequestMapping("/api/orders")
class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/create")
    public ResponseEntity<OrderService.OrderCreationResult> placeOrder(@RequestBody Map<String, Object> request) {
        Long userId = Long.valueOf(request.getOrDefault("userId", "1").toString());
        String orderNumber = (String) request.getOrDefault("orderNumber", "ORD-" + System.currentTimeMillis());

        List<OrderService.ItemDto> items = List.of(
                new OrderService.ItemDto("SKU-LAPTOP-01", "MacBook Pro M3", 1, new BigDecimal("1999.00")),
                new OrderService.ItemDto("SKU-MOUSE-02", "Wireless Mouse", 1, new BigDecimal("49.99"))
        );

        OrderService.OrderCreationResult result = orderService.placeOrder(userId, orderNumber, items);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CustomerOrder> getOrder(@PathVariable Long id) {
        return orderService.getOrderById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}/items")
    public ResponseEntity<List<OrderItem>> getOrderItems(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.getItemsForOrder(id));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<CustomerOrder>> getOrdersByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(orderService.getOrdersByUser(userId));
    }
}
