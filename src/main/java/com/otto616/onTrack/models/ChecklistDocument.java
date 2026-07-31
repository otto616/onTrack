package com.otto616.onTrack.models;

import jakarta.persistence.*;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDate;

@Entity
public class ChecklistDocument {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;

    @ManyToOne
    @JoinColumn(name = "document_type_id", nullable = false)
    private DocumentType documentType;

    private boolean received = false;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate expirationDate;

    private String fileName;

    public ChecklistDocument() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Client getClient() { return client; }
    public void setClient(Client client) { this.client = client; }

    public DocumentType getDocumentType() { return documentType; }
    public void setDocumentType(DocumentType documentType) { this.documentType = documentType; }

    public boolean isReceived() { return received; }
    public void setReceived(boolean received) { this.received = received; }

    public LocalDate getExpirationDate() { return expirationDate; }
    public void setExpirationDate(LocalDate expirationDate) { this.expirationDate = expirationDate; }

    public String getFileName() { return fileName; }
    public void setFileName(String fileName) { this.fileName = fileName; }

    @Transient
    public String getStatus() {
        if (!received) {
            return "PENDING";
        }
        if (expirationDate != null) {
            LocalDate today = LocalDate.now();
            if (expirationDate.isBefore(today)) {
                return "EXPIRED";
            }
            if (expirationDate.isBefore(today.plusDays(30))) {
                return "EXPIRING_SOON";
            }
        }
        return "OK";
    }
}