package com.otto616.onTrack.repositories;

import com.otto616.onTrack.models.DocumentVersion;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DocumentVersionRepository extends JpaRepository<DocumentVersion, Long> {
}