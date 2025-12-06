package com.dextor.sales;

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
 * Unit Tests for SaleService
 * Tests all CRUD operations and business logic for sales management
 */
@SpringBootTest
@Transactional
@DisplayName("Sale Service Tests")
class SaleServiceTest {

    @Autowired
    private SaleService saleService;

    @Autowired
    private SaleRepository saleRepository;

    @BeforeEach
    void setUp() {
        saleRepository.deleteAll();
    }

    @Test
    @DisplayName("Test 1: Create sale successfully")
    void testCreateSale_Success() {
        // Arrange
        String customerName = "John Doe";
        String itemName = "Laptop";
        Integer quantity = 2;
        Double unitPrice = 999.99;
        String notes = "Corporate sale";

        // Act
        saleService.createSale(customerName, itemName, quantity, unitPrice, notes);

        // Assert
        assertEquals(1, saleService.count());
        var sales = saleService.list(org.springframework.data.domain.PageRequest.of(0, 10));
        Sale sale = sales.get(0);
        assertEquals(customerName, sale.getCustomerName());
        assertEquals(itemName, sale.getItemName());
        assertEquals(quantity, sale.getQuantity());
        assertEquals(unitPrice, sale.getUnitPrice());
        assertEquals(1999.98, sale.getTotalAmount(), 0.01, "Total should be quantity * unitPrice");
        assertEquals(notes, sale.getNotes());
    }

    @Test
    @DisplayName("Test 2: Total amount is calculated correctly")
    void testCreateSale_TotalAmountCalculation() {
        // Arrange & Act
        saleService.createSale("Customer", "Item", 5, 25.50, null);

        // Assert
        var sales = saleService.list(org.springframework.data.domain.PageRequest.of(0, 10));
        assertEquals(127.50, sales.get(0).getTotalAmount(), 0.01);
    }

    @Test
    @DisplayName("Test 3: Create sale with null notes")
    void testCreateSale_NullNotes() {
        // Act
        saleService.createSale("Jane Smith", "Mouse", 1, 29.99, null);

        // Assert
        var sales = saleService.list(org.springframework.data.domain.PageRequest.of(0, 10));
        assertNull(sales.get(0).getNotes());
    }

    @Test
    @DisplayName("Test 4: Delete sale successfully")
    void testDeleteSale_Success() {
        // Arrange
        saleService.createSale("Customer", "Item", 1, 100.0, null);
        var sales = saleService.list(org.springframework.data.domain.PageRequest.of(0, 10));
        Long saleId = sales.get(0).getId();

        // Act
        saleService.deleteSale(saleId);

        // Assert
        assertEquals(0, saleService.count());
    }

    @Test
    @DisplayName("Test 5: Count sales correctly")
    void testCount_MultipleSales() {
        // Arrange
        saleService.createSale("Customer 1", "Item A", 1, 100.0, null);
        saleService.createSale("Customer 2", "Item B", 2, 200.0, null);
        saleService.createSale("Customer 3", "Item C", 3, 300.0, null);

        // Act
        long count = saleService.count();

        // Assert
        assertEquals(3, count);
    }

    @Test
    @DisplayName("Test 6: Get total revenue from all sales")
    void testGetTotalRevenue() {
        // Arrange
        saleService.createSale("Customer 1", "Item A", 2, 50.0, null);   // $100
        saleService.createSale("Customer 2", "Item B", 3, 75.0, null);   // $225
        saleService.createSale("Customer 3", "Item C", 1, 200.0, null);  // $200

        // Act
        double totalRevenue = saleService.getTotalRevenue();

        // Assert
        assertEquals(525.0, totalRevenue, 0.01);
    }

    @Test
    @DisplayName("Test 7: List sales with pagination")
    void testList_WithPagination() {
        // Arrange - Create 5 sales
        for (int i = 1; i <= 5; i++) {
            saleService.createSale("Customer " + i, "Item " + i, i, i * 50.0, null);
        }

        // Act
        var page1 = saleService.list(org.springframework.data.domain.PageRequest.of(0, 2));
        var page2 = saleService.list(org.springframework.data.domain.PageRequest.of(1, 2));

        // Assert
        assertEquals(2, page1.size());
        assertEquals(2, page2.size());
        assertEquals(5, saleService.count());
    }

    @Test
    @DisplayName("Test 8: Sale date is set on creation")
    void testCreateSale_SaleDateSet() {
        // Act
        saleService.createSale("Customer", "Item", 1, 100.0, null);

        // Assert
        var sales = saleService.list(org.springframework.data.domain.PageRequest.of(0, 10));
        assertNotNull(sales.get(0).getSaleDate());
    }

    @Test
    @DisplayName("Test 9: Empty sales returns zero count and revenue")
    void testCount_EmptySales() {
        // Act
        long count = saleService.count();
        double totalRevenue = saleService.getTotalRevenue();

        // Assert
        assertEquals(0, count);
        assertEquals(0.0, totalRevenue, 0.01);
    }

    @Test
    @DisplayName("Test 10: Multiple sales to same customer")
    void testMultipleSales_SameCustomer() {
        // Arrange & Act
        saleService.createSale("John Doe", "Item A", 1, 100.0, null);
        saleService.createSale("John Doe", "Item B", 2, 50.0, null);
        saleService.createSale("John Doe", "Item C", 1, 200.0, null);

        // Assert
        assertEquals(3, saleService.count());
        assertEquals(400.0, saleService.getTotalRevenue(), 0.01);
    }
}

