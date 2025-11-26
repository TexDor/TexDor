package com.dextor.order;

import java.time.Instant;

import org.jspecify.annotations.Nullable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "orders")
public class Order {

    public static final int CUSTOMER_NAME_MAX_LENGTH = 200;
    public static final int ITEM_NAME_MAX_LENGTH = 200;
    public static final int NOTES_MAX_LENGTH = 500;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "order_id")
    private Long id;

    @Column(name = "customer_name", nullable = false, length = CUSTOMER_NAME_MAX_LENGTH)
    private String customerName = "";

    @Column(name = "item_name", nullable = false, length = ITEM_NAME_MAX_LENGTH)
    private String itemName = "";

    @Column(name = "quantity", nullable = false)
    private Integer quantity = 0;

    @Column(name = "total_price", nullable = false)
    private Double totalPrice = 0.0;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private OrderStatus status = OrderStatus.PENDING;

    @Column(name = "notes", length = NOTES_MAX_LENGTH)
    @Nullable
    private String notes;

    @Column(name = "order_date", nullable = false)
    private Instant orderDate;

    protected Order() {
    }

    public Order(String customerName, String itemName, Integer quantity, Double totalPrice, Instant orderDate) {
        setCustomerName(customerName);
        setItemName(itemName);
        setQuantity(quantity);
        setTotalPrice(totalPrice);
        this.orderDate = orderDate;
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

    public Double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(Double totalPrice) {
        if (totalPrice < 0) {
            throw new IllegalArgumentException("Total price cannot be negative");
        }
        this.totalPrice = totalPrice;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
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

    public Instant getOrderDate() {
        return orderDate;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !getClass().isAssignableFrom(obj.getClass())) {
            return false;
        }
        if (obj == this) {
            return true;
        }

        Order other = (Order) obj;
        return getId() != null && getId().equals(other.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}

