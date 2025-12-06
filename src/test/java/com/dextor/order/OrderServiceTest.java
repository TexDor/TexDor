package com.dextor.order;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

/**
 * Unit Tests for OrderService
 * Tests all CRUD operations and business logic for order management
 */
@SpringBootTest
@Transactional
@DisplayName("Order Service Tests")
class OrderServiceTest {

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderRepository orderRepository;

    @BeforeEach
    void setUp() {
        orderRepository.deleteAll();
    }

    @Test
    @DisplayName("Test 1: Create order successfully")
    void testCreateOrder_Success() {
        // Arrange
        String customerName = "John Doe";
        String itemName = "Laptop";
        Integer quantity = 2;
        Double totalPrice = 1999.98;
        String notes = "Rush delivery";

        // Act
        orderService.createOrder(customerName, itemName, quantity, totalPrice, notes);

        // Assert
        assertEquals(1, orderService.count());
        var orders = orderService.list(org.springframework.data.domain.PageRequest.of(0, 10));
        Order order = orders.get(0);
        assertEquals(customerName, order.getCustomerName());
        assertEquals(itemName, order.getItemName());
        assertEquals(quantity, order.getQuantity());
        assertEquals(totalPrice, order.getTotalPrice());
        assertEquals(notes, order.getNotes());
        assertEquals(OrderStatus.PENDING, order.getStatus(), "New order should have PENDING status");
    }

    @Test
    @DisplayName("Test 2: Create order with null notes")
    void testCreateOrder_NullNotes() {
        // Act
        orderService.createOrder("Jane Smith", "Mouse", 1, 29.99, null);

        // Assert
        var orders = orderService.list(org.springframework.data.domain.PageRequest.of(0, 10));
        assertNull(orders.get(0).getNotes(), "Notes should be null when not provided");
    }

    @Test
    @DisplayName("Test 3: Update order status to PROCESSING")
    void testUpdateOrderStatus_ToProcessing() {
        // Arrange
        orderService.createOrder("Customer", "Item", 1, 100.0, null);
        var orders = orderService.list(org.springframework.data.domain.PageRequest.of(0, 10));
        Order order = orders.get(0);

        // Act
        orderService.updateOrderStatus(order, OrderStatus.PROCESSING);

        // Assert
        var updatedOrders = orderService.list(org.springframework.data.domain.PageRequest.of(0, 10));
        assertEquals(OrderStatus.PROCESSING, updatedOrders.get(0).getStatus());
    }

    @Test
    @DisplayName("Test 4: Update order status to SHIPPED")
    void testUpdateOrderStatus_ToShipped() {
        // Arrange
        orderService.createOrder("Customer", "Item", 1, 100.0, null);
        var orders = orderService.list(org.springframework.data.domain.PageRequest.of(0, 10));
        Order order = orders.get(0);

        // Act
        orderService.updateOrderStatus(order, OrderStatus.SHIPPED);

        // Assert
        var updatedOrders = orderService.list(org.springframework.data.domain.PageRequest.of(0, 10));
        assertEquals(OrderStatus.SHIPPED, updatedOrders.get(0).getStatus());
    }

    @Test
    @DisplayName("Test 5: Update order status to DELIVERED")
    void testUpdateOrderStatus_ToDelivered() {
        // Arrange
        orderService.createOrder("Customer", "Item", 1, 100.0, null);
        var orders = orderService.list(org.springframework.data.domain.PageRequest.of(0, 10));
        Order order = orders.get(0);

        // Act
        orderService.updateOrderStatus(order, OrderStatus.DELIVERED);

        // Assert
        var updatedOrders = orderService.list(org.springframework.data.domain.PageRequest.of(0, 10));
        assertEquals(OrderStatus.DELIVERED, updatedOrders.get(0).getStatus());
    }

    @Test
    @DisplayName("Test 6: Update order status to CANCELLED")
    void testUpdateOrderStatus_ToCancelled() {
        // Arrange
        orderService.createOrder("Customer", "Item", 1, 100.0, null);
        var orders = orderService.list(org.springframework.data.domain.PageRequest.of(0, 10));
        Order order = orders.get(0);

        // Act
        orderService.updateOrderStatus(order, OrderStatus.CANCELLED);

        // Assert
        var updatedOrders = orderService.list(org.springframework.data.domain.PageRequest.of(0, 10));
        assertEquals(OrderStatus.CANCELLED, updatedOrders.get(0).getStatus());
    }

    @Test
    @DisplayName("Test 7: Delete order successfully")
    void testDeleteOrder_Success() {
        // Arrange
        orderService.createOrder("Customer", "Item", 1, 100.0, null);
        var orders = orderService.list(org.springframework.data.domain.PageRequest.of(0, 10));
        Long orderId = orders.get(0).getId();

        // Act
        orderService.deleteOrder(orderId);

        // Assert
        assertEquals(0, orderService.count());
    }

    @Test
    @DisplayName("Test 8: Count orders correctly")
    void testCount_MultipleOrders() {
        // Arrange
        orderService.createOrder("Customer 1", "Item A", 1, 100.0, null);
        orderService.createOrder("Customer 2", "Item B", 2, 200.0, null);
        orderService.createOrder("Customer 3", "Item C", 3, 300.0, null);

        // Act
        long count = orderService.count();

        // Assert
        assertEquals(3, count);
    }

    @Test
    @DisplayName("Test 9: List orders with pagination")
    void testList_WithPagination() {
        // Arrange - Create 5 orders
        for (int i = 1; i <= 5; i++) {
            orderService.createOrder("Customer " + i, "Item " + i, i, i * 100.0, null);
        }

        // Act
        var page1 = orderService.list(org.springframework.data.domain.PageRequest.of(0, 2));
        var page2 = orderService.list(org.springframework.data.domain.PageRequest.of(1, 2));

        // Assert
        assertEquals(2, page1.size());
        assertEquals(2, page2.size());
        assertEquals(5, orderService.count());
    }

    @Test
    @DisplayName("Test 10: Order date is set on creation")
    void testCreateOrder_OrderDateSet() {
        // Act
        orderService.createOrder("Customer", "Item", 1, 100.0, null);

        // Assert
        var orders = orderService.list(org.springframework.data.domain.PageRequest.of(0, 10));
        assertNotNull(orders.get(0).getOrderDate(), "Order date should be set automatically");
    }

    @Test
    @DisplayName("Test 11: Status workflow PENDING -> PROCESSING -> SHIPPED -> DELIVERED")
    void testOrderStatusWorkflow_Complete() {
        // Arrange
        orderService.createOrder("Customer", "Item", 1, 100.0, null);
        var orders = orderService.list(org.springframework.data.domain.PageRequest.of(0, 10));
        Order order = orders.get(0);

        // Act & Assert - PENDING
        assertEquals(OrderStatus.PENDING, order.getStatus());

        // Act & Assert - PROCESSING
        orderService.updateOrderStatus(order, OrderStatus.PROCESSING);
        orders = orderService.list(org.springframework.data.domain.PageRequest.of(0, 10));
        assertEquals(OrderStatus.PROCESSING, orders.get(0).getStatus());

        // Act & Assert - SHIPPED
        orderService.updateOrderStatus(orders.get(0), OrderStatus.SHIPPED);
        orders = orderService.list(org.springframework.data.domain.PageRequest.of(0, 10));
        assertEquals(OrderStatus.SHIPPED, orders.get(0).getStatus());

        // Act & Assert - DELIVERED
        orderService.updateOrderStatus(orders.get(0), OrderStatus.DELIVERED);
        orders = orderService.list(org.springframework.data.domain.PageRequest.of(0, 10));
        assertEquals(OrderStatus.DELIVERED, orders.get(0).getStatus());
    }

    @Test
    @DisplayName("Test 12: Empty orders returns zero count")
    void testCount_EmptyOrders() {
        // Act
        long count = orderService.count();

        // Assert
        assertEquals(0, count);
    }
}

