package com.example.orderservice;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Repo 2 Specific Table 1: customer_orders
 * Exclusively used by repo2-order-service.
 */
@Entity
@Table(name = "customer_orders")
class CustomerOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "order_number", nullable = false, unique = true)
    private String orderNumber;

    @Column(name = "total_amount", nullable = false)
    private BigDecimal totalAmount;

    private String status;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    public CustomerOrder() {}

    public CustomerOrder(Long userId, String orderNumber, BigDecimal totalAmount, String status) {
        this.userId = userId;
        this.orderNumber = orderNumber;
        this.totalAmount = totalAmount;
        this.status = status;
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getOrderNumber() { return orderNumber; }
    public void setOrderNumber(String orderNumber) { this.orderNumber = orderNumber; }
    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}

/**
 * Repo 2 Specific Table 2: order_items
 * Exclusively used by repo2-order-service.
 */
@Entity
@Table(name = "order_items")
class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "order_id", nullable = false)
    private Long orderId;

    @Column(name = "product_sku", nullable = false)
    private String productSku;

    @Column(name = "product_name")
    private String productName;

    private Integer quantity;

    @Column(name = "unit_price", nullable = false)
    private BigDecimal unitPrice;

    public OrderItem() {}

    public OrderItem(Long orderId, String productSku, String productName, Integer quantity, BigDecimal unitPrice) {
        this.orderId = orderId;
        this.productSku = productSku;
        this.productName = productName;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getOrderId() { return orderId; }
    public void setOrderId(Long orderId) { this.orderId = orderId; }
    public String getProductSku() { return productSku; }
    public void setProductSku(String productSku) { this.productSku = productSku; }
    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }
    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
    public BigDecimal getUnitPrice() { return unitPrice; }
    public void setUnitPrice(BigDecimal unitPrice) { this.unitPrice = unitPrice; }
}
