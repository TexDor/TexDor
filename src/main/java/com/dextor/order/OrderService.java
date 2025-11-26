package com.dextor.order;

import java.time.Instant;
import java.util.List;

import org.jspecify.annotations.Nullable;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderService {

    private final OrderRepository orderRepository;

    OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Transactional
    public void createOrder(String customerName, String itemName, Integer quantity, Double totalPrice, @Nullable String notes) {
        var order = new Order(customerName, itemName, quantity, totalPrice, Instant.now());
        order.setNotes(notes);
        orderRepository.saveAndFlush(order);
    }

    @Transactional
    public void updateOrderStatus(Order order, OrderStatus status) {
        order.setStatus(status);
        orderRepository.saveAndFlush(order);
    }

    @Transactional
    public void deleteOrder(Long id) {
        orderRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<Order> list(Pageable pageable) {
        return orderRepository.findAllBy(pageable).toList();
    }

    @Transactional(readOnly = true)
    public long count() {
        return orderRepository.count();
    }
}

