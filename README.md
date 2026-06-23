# Inventory & Order Management API

A Spring Boot REST API for managing inventory and customer orders.

Built using:

- Java 17
- Spring Boot 3
- Spring Data JPA
- H2 Database (File Mode)
- Maven
- Lombok

---

# Features

## Catalog Management

- Create categories
- Create products
- List products
- Product stock adjustments
- Low-stock reporting

## Customer Management

- Create customers

## Order Management

- Create draft orders
- Add items to draft orders
- Update item quantities
- Remove items
- Confirm orders
- Ship orders
- Deliver orders
- Cancel orders

## Stock Protection

- Stock is not reserved while orders are DRAFT.
- Stock is deducted only when orders are CONFIRMED.
- Stock is restored when CONFIRMED or SHIPPED orders are cancelled.
- Overselling is prevented.

---

# Technology Stack

| Technology | Version |
|------------|---------|
| Java | 17 |
| Spring Boot | 3.x |
| Spring Data JPA | Latest |
| H2 Database | File Mode |
| Maven | Latest |

---

# Project Structure

```
com.mariya.inventory
│
├── category
├── product
├── customer
├── order
├── exception
└── InventoryApplication
```

Each module contains:

```
controller
dto
entity
repository
service
```

---

# Database Schema

## Categories

| Column | Type |
|----------|------|
| id | BIGINT |
| name | VARCHAR |

---

## Products

| Column | Type |
|----------|------|
| id | BIGINT |
| name | VARCHAR |
| sku | VARCHAR |
| price | DECIMAL |
| stock_quantity | INTEGER |
| category_id | BIGINT |

---

## Customers

| Column | Type |
|----------|------|
| id | BIGINT |
| name | VARCHAR |
| email | VARCHAR |

---

## Customer Orders

| Column | Type |
|----------|------|
| id | BIGINT |
| customer_id | BIGINT |
| status | VARCHAR |
| total_amount | DECIMAL |
| created_at | TIMESTAMP |
| confirmed_at | TIMESTAMP |
| shipped_at | TIMESTAMP |
| delivered_at | TIMESTAMP |
| cancelled_at | TIMESTAMP |

---

## Order Items

| Column | Type |
|----------|------|
| id | BIGINT |
| order_id | BIGINT |
| product_id | BIGINT |
| quantity | INTEGER |
| unit_price | DECIMAL |
| line_total | DECIMAL |

---

# Entity Relationships

```
Category
    1
    |
    *
Product

Customer
    1
    |
    *
CustomerOrder

CustomerOrder
    1
    |
    *
OrderItem

Product
    1
    |
    *
OrderItem
```

---

# Order Lifecycle

```
DRAFT
   ↓
CONFIRMED
   ↓
SHIPPED
   ↓
DELIVERED

CONFIRMED
   ↓
CANCELLED

SHIPPED
   ↓
CANCELLED
```

Delivered orders cannot be cancelled.

---

# Business Rules

### Draft Orders

Adding, updating, or removing items from draft orders does NOT affect stock.

### Order Confirmation

During confirmation:

1. Validate stock availability.
2. Reject order if any product lacks stock.
3. Deduct inventory.
4. Capture unit price.
5. Calculate line totals.
6. Calculate order total.
7. Change status to CONFIRMED.

### Cancellation

Cancelling a CONFIRMED or SHIPPED order restores inventory.

### Overselling Prevention

Products cannot be sold below zero stock.

---

# API Endpoints

## Categories

### Create Category

```
POST /api/categories
```

### Get Categories

```
GET /api/categories
```

---

## Products

### Create Product

```
POST /api/products
```

### Get Product

```
GET /api/products/{id}
```

### List Products

```
GET /api/products
```

### Adjust Stock

```
POST /api/products/{id}/stock-adjustments
```

### Low Stock Report

```
GET /api/products/low-stock?threshold=5
```

---

## Customers

### Create Customer

```
POST /api/customers
```

---

## Orders

### Create Draft Order

```
POST /api/customers/{customerId}/orders
```

### Add Item

```
POST /api/orders/{orderId}/items
```

### Update Item

```
PATCH /api/orders/{orderId}/items/{itemId}
```

### Remove Item

```
DELETE /api/orders/{orderId}/items/{itemId}
```

### Get Order

```
GET /api/orders/{orderId}
```

### Confirm Order

```
POST /api/orders/{orderId}/confirm
```

### Update Status

```
POST /api/orders/{orderId}/status
```

---

# Exception Handling

| Status Code | Description |
|-------------|-------------|
| 400 | Validation Error |
| 404 | Resource Not Found |
| 409 | Business Conflict |

---

# Running the Project

Clone:

```bash
git clone <repository-url>
```

Run:

```bash
mvn spring-boot:run
```

Application:

```
http://localhost:8080
```

H2 Console:

```
http://localhost:8080/h2-console
```

Connection:

```
JDBC URL:
jdbc:h2:file:./data/inventorydb

Username:
sa

Password:
(empty)
```

---

# Assumptions

- Category names are unique.
- Product SKUs are unique.
- Customer emails are unique.
- Orders begin in DRAFT status.
- Stock is deducted only during confirmation.
- Delivered orders cannot be cancelled.
- Prices are copied into order items during confirmation.
- Order totals are derived from order items.
