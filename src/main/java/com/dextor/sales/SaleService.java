package com.dextor.sales;

import java.time.Instant;
import java.util.List;

import org.jspecify.annotations.Nullable;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SaleService {

    private final SaleRepository saleRepository;

    SaleService(SaleRepository saleRepository) {
        this.saleRepository = saleRepository;
    }

    @Transactional
    public void createSale(String customerName, String itemName, Integer quantity, Double unitPrice, @Nullable String notes) {
        var sale = new Sale(customerName, itemName, quantity, unitPrice, Instant.now());
        sale.setNotes(notes);
        saleRepository.saveAndFlush(sale);
    }

    @Transactional
    public void deleteSale(Long id) {
        saleRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<Sale> list(Pageable pageable) {
        return saleRepository.findAllBy(pageable).toList();
    }

    @Transactional(readOnly = true)
    public long count() {
        return saleRepository.count();
    }

    @Transactional(readOnly = true)
    public double getTotalRevenue() {
        return saleRepository.findAll().stream()
                .mapToDouble(Sale::getTotalAmount)
                .sum();
    }
}

