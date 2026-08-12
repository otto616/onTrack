package com.otto616.onTrack.repositories;

import com.otto616.onTrack.models.ChecklistDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;

public interface ChecklistDocumentRepository extends JpaRepository<ChecklistDocument, Long> {

    List<ChecklistDocument> findByExpirationDateLessThanEqual(LocalDate date);
    long countByReceivedFalse();
    long countByExpirationDateLessThan(LocalDate date);

    List<ChecklistDocument> findByProviderId(Long providerId);
    long countByProviderIdAndReceivedFalse(Long providerId);
    long countByProviderIdAndExpirationDateLessThan(Long providerId, LocalDate date);
    List<ChecklistDocument> findByWorkerId(Long workerId);
    List<ChecklistDocument> findByMachineryId(Long machineryId);
}