package com.easyservice.backend.repository;

import com.easyservice.backend.model.Transaction;
import com.easyservice.backend.model.enums.TransactionStatus;
import java.util.List;
import java.util.Optional;

public interface TransactionRepository {
    Transaction save(Transaction transaction);
    Optional<Transaction> findById(String id);
    List<Transaction> findAll();
    List<Transaction> findByCustomerId(String customerId);
    List<Transaction> findByListingId(String listingId);
    List<Transaction> findByStatus(TransactionStatus status);
    void clear();
}
