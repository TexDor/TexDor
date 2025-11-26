# TexDor - Inventory Application

## Overview

This is an inventory management application built with Vaadin and Spring Boot. The application allows you to manage inventory items, track orders, and monitor sales.

## Features

### 1. Home (Inventory Management)

**Route:** `/` (root)
**Icon:** Home
**Features:**

- View total number of inventory items
- View total quantity of all items in stock
- Add new inventory items with name, description, quantity, and price
- View all inventory items in a grid with sortable columns
- Automatic currency formatting for prices

### 2. Order Tracker

**Route:** `/orders`
**Icon:** Clipboard Text
**Features:**

- View total number of orders
- Create new orders with customer name, item name, quantity, and total price
- Track order status (Pending, Processing, Shipped, Delivered, Cancelled)
- Update order status directly from the grid
- View order history with timestamps
- Add optional notes to orders

### 3. Sales Tracker

**Route:** `/sales`
**Icon:** Chart Line
**Features:**

- View total number of sales
- View total revenue from all sales
- Record new sales with customer name, item name, quantity, and unit price
- Automatic calculation of total amount
- View sales history with timestamps
- Add optional notes to sales

## Technical Structure

### Packages

#### `com.dextor.inventory`

- `InventoryItem.java` - Entity representing inventory items
- `InventoryRepository.java` - JPA repository for inventory data access
- `InventoryService.java` - Service layer for inventory business logic
- `ui/HomeView.java` - Main inventory management view

#### `com.dextor.order`

- `Order.java` - Entity representing orders
- `OrderStatus.java` - Enum for order statuses
- `OrderRepository.java` - JPA repository for order data access
- `OrderService.java` - Service layer for order business logic
- `ui/OrderTrackerView.java` - Order tracking view

#### `com.dextor.sales`

- `Sale.java` - Entity representing sales
- `SaleRepository.java` - JPA repository for sales data access
- `SaleService.java` - Service layer for sales business logic
- `ui/SalesTrackerView.java` - Sales tracking view

## Running the Application

```bash
# Development mode
./mvnw

# Or explicitly
./mvnw spring-boot:run
```

The application will be available at: http://localhost:8080

## Database

The application uses an H2 in-memory database for development. All data is reset when the application restarts.

## Navigation

The left sidebar contains three menu items:

1. **Home** - Inventory management
2. **Order Tracker** - Order management
3. **Sales Tracker** - Sales management

All navigation is automatically handled by Vaadin's `@Menu` annotation system.
