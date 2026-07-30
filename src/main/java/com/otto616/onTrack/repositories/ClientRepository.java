package com.otto616.onTrack.repositories;

import com.otto616.onTrack.models.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {
    List<Client> findByNameContainingIgnoreCaseOrTaxIdContainingIgnoreCase(String name, String taxId);
}