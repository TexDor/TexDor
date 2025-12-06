package com.dextor.inventory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

/**
 * Unit Tests for InventoryService
 * Tests all CRUD operations and business logic for inventory management
 */
@SpringBootTest
@Transactional
@DisplayName("Inventory Service Tests")
class InventoryServiceTest {

    @Autowired
    private InventoryService inventoryService;

    @Autowired
    private InventoryRepository inventoryRepository;

    @BeforeEach
    void setUp() {
        // Clear database before each test
        inventoryRepository.deleteAll();
    }

    @Test
    @DisplayName("Test 1: Create item successfully")
    void testCreateItem_Success() {
        // Arrange
        String name = "Test Laptop";
        String description = "High-performance laptop";
        Integer quantity = 10;
        Double price = 999.99;

        // Act
        inventoryService.createItem(name, description, quantity, price);

        // Assert
        long count = inventoryService.count();
        assertEquals(1, count, "Should have 1 item after creation");
        
        var items = inventoryService.list(org.springframework.data.domain.PageRequest.of(0, 10));
        assertEquals(1, items.size());
        assertEquals(name, items.get(0).getName());
        assertEquals(description, items.get(0).getDescription());
        assertEquals(quantity, items.get(0).getQuantity());
        assertEquals(price, items.get(0).getPrice());
    }

    @Test
    @DisplayName("Test 2: Create item with null description")
    void testCreateItem_NullDescription() {
        // Arrange
        String name = "Test Mouse";
        Integer quantity = 5;
        Double price = 29.99;

        // Act
        inventoryService.createItem(name, null, quantity, price);

        // Assert
        assertEquals(1, inventoryService.count());
        var items = inventoryService.list(org.springframework.data.domain.PageRequest.of(0, 10));
        assertNull(items.get(0).getDescription(), "Description should be null");
    }

    @Test
    @DisplayName("Test 3: Update item successfully")
    void testUpdateItem_Success() {
        // Arrange - Create initial item
        inventoryService.createItem("Keyboard", "Mechanical", 20, 89.99);
        var items = inventoryService.list(org.springframework.data.domain.PageRequest.of(0, 10));
        InventoryItem item = items.get(0);
        
        // Act - Update the item
        item.setName("Gaming Keyboard");
        item.setQuantity(15);
        item.setPrice(129.99);
        inventoryService.updateItem(item);

        // Assert
        var updatedItems = inventoryService.list(org.springframework.data.domain.PageRequest.of(0, 10));
        assertEquals(1, updatedItems.size());
        assertEquals("Gaming Keyboard", updatedItems.get(0).getName());
        assertEquals(15, updatedItems.get(0).getQuantity());
        assertEquals(129.99, updatedItems.get(0).getPrice());
    }

    @Test
    @DisplayName("Test 4: Delete item successfully")
    void testDeleteItem_Success() {
        // Arrange
        inventoryService.createItem("Monitor", "27-inch 4K", 8, 399.99);
        var items = inventoryService.list(org.springframework.data.domain.PageRequest.of(0, 10));
        Long itemId = items.get(0).getId();

        // Act
        inventoryService.deleteItem(itemId);

        // Assert
        assertEquals(0, inventoryService.count(), "Should have 0 items after deletion");
    }

    @Test
    @DisplayName("Test 5: Count items correctly")
    void testCount_MultipleItems() {
        // Arrange
        inventoryService.createItem("Item 1", "Description 1", 10, 10.0);
        inventoryService.createItem("Item 2", "Description 2", 20, 20.0);
        inventoryService.createItem("Item 3", "Description 3", 30, 30.0);

        // Act
        long count = inventoryService.count();

        // Assert
        assertEquals(3, count, "Should have exactly 3 items");
    }

    @Test
    @DisplayName("Test 6: Get total quantity across all items")
    void testGetTotalQuantity() {
        // Arrange
        inventoryService.createItem("Item A", null, 10, 100.0);
        inventoryService.createItem("Item B", null, 25, 200.0);
        inventoryService.createItem("Item C", null, 15, 150.0);

        // Act
        int totalQuantity = inventoryService.getTotalQuantity();

        // Assert
        assertEquals(50, totalQuantity, "Total quantity should be 10 + 25 + 15 = 50");
    }

    @Test
    @DisplayName("Test 7: List items with pagination")
    void testList_WithPagination() {
        // Arrange - Create 5 items
        for (int i = 1; i <= 5; i++) {
            inventoryService.createItem("Item " + i, "Desc " + i, i * 10, i * 100.0);
        }

        // Act - Get first page with 2 items
        var page1 = inventoryService.list(org.springframework.data.domain.PageRequest.of(0, 2));
        var page2 = inventoryService.list(org.springframework.data.domain.PageRequest.of(1, 2));

        // Assert
        assertEquals(2, page1.size(), "First page should have 2 items");
        assertEquals(2, page2.size(), "Second page should have 2 items");
        assertEquals(5, inventoryService.count(), "Total should still be 5 items");
    }

    @Test
    @DisplayName("Test 8: Timestamps are set correctly on creation")
    void testCreateItem_TimestampsSet() {
        // Arrange & Act
        inventoryService.createItem("Timestamped Item", "Test", 1, 1.0);

        // Assert
        var items = inventoryService.list(org.springframework.data.domain.PageRequest.of(0, 10));
        InventoryItem item = items.get(0);
        assertNotNull(item.getCreatedAt(), "Created timestamp should be set");
        assertNotNull(item.getUpdatedAt(), "Updated timestamp should be set");
    }

    @Test
    @DisplayName("Test 9: Updated timestamp changes on update")
    void testUpdateItem_UpdatesTimestamp() throws InterruptedException {
        // Arrange
        inventoryService.createItem("Test Item", "Test", 1, 1.0);
        var items = inventoryService.list(org.springframework.data.domain.PageRequest.of(0, 10));
        InventoryItem item = items.get(0);
        var originalUpdatedAt = item.getUpdatedAt();
        
        // Wait a bit to ensure timestamp difference
        Thread.sleep(100);

        // Act
        item.setQuantity(5);
        inventoryService.updateItem(item);

        // Assert
        var updatedItems = inventoryService.list(org.springframework.data.domain.PageRequest.of(0, 10));
        assertTrue(updatedItems.get(0).getUpdatedAt().isAfter(originalUpdatedAt),
                "Updated timestamp should be after original");
    }

    @Test
    @DisplayName("Test 10: Empty inventory returns zero count")
    void testCount_EmptyInventory() {
        // Act
        long count = inventoryService.count();
        int totalQuantity = inventoryService.getTotalQuantity();

        // Assert
        assertEquals(0, count, "Empty inventory should have 0 count");
        assertEquals(0, totalQuantity, "Empty inventory should have 0 total quantity");
    }
}

