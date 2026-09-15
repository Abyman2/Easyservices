package com.easyservice.backend.repository;

import com.easyservice.backend.model.Transaction;
import com.easyservice.backend.model.enums.TransactionStatus;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Repository
public class InMemoryTransactionRepository implements TransactionRepository {
    private final Map<String, Transaction> store = new ConcurrentHashMap<>();

    @Override
    public Transaction save(Transaction transaction) {
        if (transaction.getId() == null || transaction.getId().isEmpty()) {
            transaction.setId(java.util.UUID.randomUUID().toString());
        }
        store.put(transaction.getId(), transaction);
        return transaction;
    }

    @Override
    public Optional<Transaction> findById(String id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public List<Transaction> findAll() {
        return new ArrayList<>(store.values());
    }

    @Override
    public List<Transaction> findByCustomerId(String customerId) {
        return store.values().stream()
                .filter(t -> t.getCustomerId() != null && t.getCustomerId().equals(customerId))
                .collect(Collectors.toList());
    }

    @Override
    public List<Transaction> findByListingId(String listingId) {
        return store.values().stream()
                .filter(t -> t.getListingId() != null && t.getListingId().equals(listingId))
                .collect(Collectors.toList());
    }

    @Override
    public List<Transaction> findByStatus(TransactionStatus status) {
        return store.values().stream()
                .filter(t -> t.getStatus() == status)
                .collect(Collectors.toList());
    }

    @Override
    public void clear() {
        store.clear();
    }
}
