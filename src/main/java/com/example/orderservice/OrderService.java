package com.example.orderservice;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

// ---- Repositories for Repo 2 Specific Tables ----
@Repository
interface OrderRepository extends JpaRepository<CustomerOrder, Long> {
    List<CustomerOrder> findByUserId(Long userId);
    Optional<CustomerOrder> findByOrderNumber(String orderNumber);
}

@Repository
interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    List<OrderItem> findByOrderId(Long orderId);
}

// ---- Repositories for the 3 Common Tables ----
@Repository
interface UserRepository extends JpaRepository<AppUser, Long> {}

@Repository
interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
    List<AuditLog> findByServiceName(String serviceName);
}

@Repository
interface NotificationRepository extends JpaRepository<SystemNotification, Long> {
    List<SystemNotification> findByUserId(Long userId);
}

// ---- Service orchestrating order operations and common table integration ----
@Service
@Transactional
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final UserRepository userRepository;
    private final AuditLogRepository auditLogRepository;
    private final NotificationRepository notificationRepository;

    public OrderService(OrderRepository orderRepository,
                        OrderItemRepository orderItemRepository,
                        UserRepository userRepository,
                        AuditLogRepository auditLogRepository,
                        NotificationRepository notificationRepository) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.userRepository = userRepository;
        this.auditLogRepository = auditLogRepository;
        this.notificationRepository = notificationRepository;
    }

    /**
     * Functional flow demonstrating:
     * 1. Querying Common Table 1 (app_users) to validate user
     * 2. Inserting into Repo 2 Specific Table 1 (customer_orders)
     * 3. Inserting into Repo 2 Specific Table 2 (order_items)
     * 4. Inserting into Common Table 2 (audit_logs)
     * 5. Inserting into Common Table 3 (system_notifications)
     */
    public OrderCreationResult placeOrder(Long userId, String orderNumber, List<ItemDto> items) {
        // 1. Verify user exists in Common Table: app_users
        AppUser user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User with ID " + userId + " not found in app_users table"));

        // Calculate total amount
        BigDecimal total = items.stream()
                .map(i -> i.unitPrice().multiply(BigDecimal.valueOf(i.quantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 2. Save in Specific Table 1: customer_orders
        CustomerOrder order = orderRepository.save(new CustomerOrder(user.getId(), orderNumber, total, "CONFIRMED"));

        // 3. Save in Specific Table 2: order_items
        for (ItemDto item : items) {
            orderItemRepository.save(new OrderItem(order.getId(), item.sku(), item.name(), item.quantity(), item.unitPrice()));
        }

        // 4. Save in Common Table 2: audit_logs
        AuditLog auditLog = auditLogRepository.save(new AuditLog(
                "order-service",
                "ORDER_CREATED",
                "ORDER",
                order.getId(),
                "Order " + orderNumber + " placed for user " + user.getUsername() + ", total: $" + total
        ));

        // 5. Save in Common Table 3: system_notifications
        SystemNotification notification = notificationRepository.save(new SystemNotification(
                user.getId(),
                "SMS",
                "Order Confirmation: #" + orderNumber,
                "Thank you! Your order #" + orderNumber + " of $" + total + " has been received.",
                "DISPATCHED"
        ));

        return new OrderCreationResult(order.getId(), order.getOrderNumber(), total,
                auditLog.getId(), notification.getId());
    }

    @Transactional(readOnly = true)
    public Optional<CustomerOrder> getOrderById(Long id) {
        return orderRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public List<OrderItem> getItemsForOrder(Long orderId) {
        return orderItemRepository.findByOrderId(orderId);
    }

    @Transactional(readOnly = true)
    public List<CustomerOrder> getOrdersByUser(Long userId) {
        return orderRepository.findByUserId(userId);
    }

    public record ItemDto(String sku, String name, int quantity, BigDecimal unitPrice) {}

    public record OrderCreationResult(
            Long orderId,
            String orderNumber,
            BigDecimal totalAmount,
            Long auditLogId,
            Long notificationId
    ) {}
}
