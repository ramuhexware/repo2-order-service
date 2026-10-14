# Repo 2: Order Service (`repo2-order-service`)

Spring Boot Microservice managing **2 repo-specific tables** and accessing the **3 shared common tables** in PostgreSQL:

## Tables
- **2 Repo-Specific Tables** (used only in this repository):
  1. `customer_orders` (Order header, user reference, total amount, status)
  2. `order_items` (Order line items, SKU, quantity, unit price)
- **3 Shared Common Tables** (used across all 3 microservices):
  1. `app_users` (Validates user identity before placing order)
  2. `audit_logs` (Records `ORDER_CREATED` audit events)
  3. `system_notifications` (Sends customer order confirmation notification)

## Port
`8082`

## SQL Schema Files
- **`schema-repo2-order-service.sql`**: DDL and seed data for Repo 2 specific tables (`customer_orders`, `order_items`).

## Functional Flow
1. Receives order creation request with line items.
2. Validates user existence against common `app_users` table.
3. Inserts order record into `customer_orders`.
4. Inserts items into `order_items`.
5. Logs audit entry into common `audit_logs` table (`serviceName="order-service"`).
6. Dispatches notification into common `system_notifications` table.

## Running the Application
```bash
mvn spring-boot:run
```

## REST Endpoints
- `POST /api/orders/create` - Place new order (updates customer_orders, order_items, audit_logs, system_notifications)
- `GET  /api/orders/{id}` - Fetch order by ID
- `GET  /api/orders/{id}/items` - Fetch line items for order
- `GET  /api/orders/user/{userId}` - Fetch all orders for a user
