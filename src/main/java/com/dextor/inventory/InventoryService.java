package com.dextor.inventory;

import java.time.Instant;
import java.util.List;

import org.jspecify.annotations.Nullable;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class InventoryService {

    private final InventoryRepository inventoryRepository;

    InventoryService(InventoryRepository inventoryRepository) {
        this.inventoryRepository = inventoryRepository;
    }

    @Transactional
    public void createItem(String name, @Nullable String description, Integer quantity, Double price) {
        var item = new InventoryItem(name, quantity, price, Instant.now());
        item.setDescription(description);
        inventoryRepository.saveAndFlush(item);
    }

    @Transactional
    public void updateItem(InventoryItem item) {
        item.setUpdatedAt(Instant.now());
        inventoryRepository.saveAndFlush(item);
    }

    @Transactional
    public void deleteItem(Long id) {
        inventoryRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<InventoryItem> list(Pageable pageable) {
        return inventoryRepository.findAllBy(pageable).toList();
    }

    @Transactional(readOnly = true)
    public long count() {
        return inventoryRepository.count();
    }

    @Transactional(readOnly = true)
    public int getTotalQuantity() {
        return inventoryRepository.findAll().stream()
                .mapToInt(InventoryItem::getQuantity)
                .sum();
    }
}

