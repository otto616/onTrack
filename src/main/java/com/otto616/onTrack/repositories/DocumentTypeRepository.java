package com.otto616.onTrack.repositories;

import com.otto616.onTrack.models.DocumentType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DocumentTypeRepository extends JpaRepository<DocumentType, Long> {

    java.util.List<DocumentType> findByProviderType(com.otto616.onTrack.models.Enums.ProviderType clientType);

}