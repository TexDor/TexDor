package com.dextor.inventory;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Unit Tests for InventoryItem Entity
 * Tests validation rules and business logic in the entity
 */
@DisplayName("Inventory Item Entity Tests")
class InventoryItemTest {

    @Test
    @DisplayName("Test 1: Create valid inventory item")
    void testCreateInventoryItem_Valid() {
        // Arrange & Act
        InventoryItem item = new InventoryItem("Laptop", 10, 999.99, Instant.now());
        item.setDescription("High-performance laptop");

        // Assert
        assertEquals("Laptop", item.getName());
        assertEquals(10, item.getQuantity());
        assertEquals(999.99, item.getPrice());
        assertEquals("High-performance laptop", item.getDescription());
    }

    @Test
    @DisplayName("Test 2: Name exceeds max length throws exception")
    void testSetName_ExceedsMaxLength() {
        // Arrange
        InventoryItem item = new InventoryItem("Valid Name", 1, 1.0, Instant.now());
        String longName = "a".repeat(InventoryItem.NAME_MAX_LENGTH + 1);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            item.setName(longName);
        }, "Should throw exception when name exceeds max length");
    }

    @Test
    @DisplayName("Test 3: Description exceeds max length throws exception")
    void testSetDescription_ExceedsMaxLength() {
        // Arrange
        InventoryItem item = new InventoryItem("Item", 1, 1.0, Instant.now());
        String longDescription = "a".repeat(InventoryItem.DESCRIPTION_MAX_LENGTH + 1);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            item.setDescription(longDescription);
        }, "Should throw exception when description exceeds max length");
    }

    @Test
    @DisplayName("Test 4: Negative quantity throws exception")
    void testSetQuantity_Negative() {
        // Arrange
        InventoryItem item = new InventoryItem("Item", 1, 1.0, Instant.now());

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            item.setQuantity(-1);
        }, "Should throw exception for negative quantity");
    }

    @Test
    @DisplayName("Test 5: Zero quantity is valid")
    void testSetQuantity_Zero() {
        // Arrange
        InventoryItem item = new InventoryItem("Item", 1, 1.0, Instant.now());

        // Act & Assert
        assertDoesNotThrow(() -> item.setQuantity(0));
        assertEquals(0, item.getQuantity());
    }

    @Test
    @DisplayName("Test 6: Negative price throws exception")
    void testSetPrice_Negative() {
        // Arrange
        InventoryItem item = new InventoryItem("Item", 1, 1.0, Instant.now());

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            item.setPrice(-0.01);
        }, "Should throw exception for negative price");
    }

    @Test
    @DisplayName("Test 7: Zero price is valid")
    void testSetPrice_Zero() {
        // Arrange
        InventoryItem item = new InventoryItem("Item", 1, 1.0, Instant.now());

        // Act & Assert
        assertDoesNotThrow(() -> item.setPrice(0.0));
        assertEquals(0.0, item.getPrice());
    }

    @Test
    @DisplayName("Test 8: Null description is valid")
    void testSetDescription_Null() {
        // Arrange
        InventoryItem item = new InventoryItem("Item", 1, 1.0, Instant.now());

        // Act & Assert
        assertDoesNotThrow(() -> item.setDescription(null));
        assertNull(item.getDescription());
    }

    @Test
    @DisplayName("Test 9: Name at max length is valid")
    void testSetName_AtMaxLength() {
        // Arrange
        InventoryItem item = new InventoryItem("Item", 1, 1.0, Instant.now());
        String maxLengthName = "a".repeat(InventoryItem.NAME_MAX_LENGTH);

        // Act & Assert
        assertDoesNotThrow(() -> item.setName(maxLengthName));
        assertEquals(maxLengthName, item.getName());
    }

    @Test
    @DisplayName("Test 10: Equals and hashCode for same ID")
    void testEqualsAndHashCode_SameId() {
        // This test demonstrates the equals/hashCode contract
        // Note: IDs are set by JPA, so we can't directly test equality here
        // This test is more of a placeholder for integration tests
        InventoryItem item1 = new InventoryItem("Item", 1, 1.0, Instant.now());
        InventoryItem item2 = new InventoryItem("Item", 1, 1.0, Instant.now());

        // Items without IDs should not be equal
        assertNotEquals(item1, item2);
    }
}

