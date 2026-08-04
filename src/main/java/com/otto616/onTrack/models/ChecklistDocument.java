package com.otto616.onTrack.models;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
public class ChecklistDocument {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Provider provider;

    @ManyToOne
    private DocumentType documentType;

    private boolean received;

    private LocalDate expirationDate;

    private String fileName;

    public ChecklistDocument() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Provider getProvider() { return provider; }
    public void setProvider(Provider provider) { this.provider = provider; }

    public DocumentType getDocumentType() { return documentType; }
    public void setDocumentType(DocumentType documentType) { this.documentType = documentType; }

    public boolean isReceived() { return received; }
    public void setReceived(boolean received) { this.received = received; }

    public LocalDate getExpirationDate() { return expirationDate; }
    public void setExpirationDate(LocalDate expirationDate) { this.expirationDate = expirationDate; }

    public String getFileName() { return fileName; }
    public void setFileName(String fileName) { this.fileName = fileName; }
}