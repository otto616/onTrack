package com.otto616.onTrack.repositories;

import com.otto616.onTrack.models.ChecklistDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ChecklistDocumentRepository extends JpaRepository<ChecklistDocument, Long> {

    List<ChecklistDocument> findByClientId(Long clientId);
    List<ChecklistDocument> findByExpirationDateLessThanEqual(LocalDate date);
    long countByReceivedFalse();
    long countByExpirationDateLessThan(LocalDate date);
}