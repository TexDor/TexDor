package com.dextor.sales;

import java.time.Instant;

import org.jspecify.annotations.Nullable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "sales")
public class Sale {

    public static final int CUSTOMER_NAME_MAX_LENGTH = 200;
    public static final int ITEM_NAME_MAX_LENGTH = 200;
    public static final int NOTES_MAX_LENGTH = 500;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "sale_id")
    private Long id;

    @Column(name = "customer_name", nullable = false, length = CUSTOMER_NAME_MAX_LENGTH)
    private String customerName = "";

    @Column(name = "item_name", nullable = false, length = ITEM_NAME_MAX_LENGTH)
    private String itemName = "";

    @Column(name = "quantity", nullable = false)
    private Integer quantity = 0;

    @Column(name = "unit_price", nullable = false)
    private Double unitPrice = 0.0;

    @Column(name = "total_amount", nullable = false)
    private Double totalAmount = 0.0;

    @Column(name = "notes", length = NOTES_MAX_LENGTH)
    @Nullable
    private String notes;

    @Column(name = "sale_date", nullable = false)
    private Instant saleDate;

    protected Sale() {
    }

    public Sale(String customerName, String itemName, Integer quantity, Double unitPrice, Instant saleDate) {
        setCustomerName(customerName);
        setItemName(itemName);
        setQuantity(quantity);
        setUnitPrice(unitPrice);
        this.totalAmount = quantity * unitPrice;
        this.saleDate = saleDate;
    }

    public @Nullable Long getId() {
        return id;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        if (customerName.length() > CUSTOMER_NAME_MAX_LENGTH) {
            throw new IllegalArgumentException("Customer name length exceeds " + CUSTOMER_NAME_MAX_LENGTH);
        }
        this.customerName = customerName;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        if (itemName.length() > ITEM_NAME_MAX_LENGTH) {
            throw new IllegalArgumentException("Item name length exceeds " + ITEM_NAME_MAX_LENGTH);
        }
        this.itemName = itemName;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }
        this.quantity = quantity;
    }

    public Double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(Double unitPrice) {
        if (unitPrice < 0) {
            throw new IllegalArgumentException("Unit price cannot be negative");
        }
        this.unitPrice = unitPrice;
    }

    public Double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(Double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public @Nullable String getNotes() {
        return notes;
    }

    public void setNotes(@Nullable String notes) {
        if (notes != null && notes.length() > NOTES_MAX_LENGTH) {
            throw new IllegalArgumentException("Notes length exceeds " + NOTES_MAX_LENGTH);
        }
        this.notes = notes;
    }

    public Instant getSaleDate() {
        return saleDate;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !getClass().isAssignableFrom(obj.getClass())) {
            return false;
        }
        if (obj == this) {
            return true;
        }

        Sale other = (Sale) obj;
        return getId() != null && getId().equals(other.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}

