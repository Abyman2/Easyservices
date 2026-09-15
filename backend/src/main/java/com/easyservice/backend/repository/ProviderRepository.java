package com.easyservice.backend.repository;

import com.easyservice.backend.model.Provider;
import java.util.List;
import java.util.Optional;

public interface ProviderRepository {
    Provider save(Provider provider);
    Optional<Provider> findById(String id);
    Optional<Provider> findByEmail(String email);
    List<Provider> findAll();
    void clear();
}
