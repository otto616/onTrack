package com.otto616.onTrack.repositories;

import com.otto616.onTrack.models.Worker;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface WorkerRepository extends JpaRepository<Worker, Long> {
    List<Worker> findByProviderId(Long providerId);
}