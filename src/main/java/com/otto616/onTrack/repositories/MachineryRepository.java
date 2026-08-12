package com.otto616.onTrack.repositories;

import com.otto616.onTrack.models.Machinery;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface MachineryRepository extends JpaRepository<Machinery, Long> {
    List<Machinery> findByProviderId(Long providerId);
    List<Machinery> findByNameContainingIgnoreCaseOrInternalCodeContainingIgnoreCaseOrSerialNumberContainingIgnoreCase(String name, String code, String serial);
}