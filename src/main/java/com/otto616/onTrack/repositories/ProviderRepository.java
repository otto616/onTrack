package com.otto616.onTrack.repositories;

import com.otto616.onTrack.models.Provider;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProviderRepository extends JpaRepository<Provider, Long> {
    List<Provider> findByNameContainingIgnoreCaseOrTaxIdContainingIgnoreCase(String name, String taxId);
}