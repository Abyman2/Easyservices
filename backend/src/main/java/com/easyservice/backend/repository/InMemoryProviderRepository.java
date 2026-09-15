package com.easyservice.backend.repository;

import com.easyservice.backend.model.Provider;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class InMemoryProviderRepository implements ProviderRepository {
    private final Map<String, Provider> store = new ConcurrentHashMap<>();

    @Override
    public Provider save(Provider provider) {
        if (provider.getId() == null || provider.getId().isEmpty()) {
            provider.setId(java.util.UUID.randomUUID().toString());
        }
        store.put(provider.getId(), provider);
        return provider;
    }

    @Override
    public Optional<Provider> findById(String id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public Optional<Provider> findByEmail(String email) {
        if (email == null) return Optional.empty();
        return store.values().stream()
                .filter(p -> email.equalsIgnoreCase(p.getEmail()))
                .findFirst();
    }

    @Override
    public List<Provider> findAll() {
        return new ArrayList<>(store.values());
    }

    @Override
    public void clear() {
        store.clear();
    }
}
