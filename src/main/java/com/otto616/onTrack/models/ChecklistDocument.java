package com.otto616.onTrack.models;

import jakarta.persistence.*;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
public class ChecklistDocument {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Provider provider;

    @ManyToOne
    private Worker worker;

    @ManyToOne
    private Machinery machinery;

    @ManyToOne
    private DocumentType documentType;

    private boolean received;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate expirationDate;

    private String fileName;

    private boolean exempt = false;

    @OneToMany(mappedBy = "checklistDocument", cascade = CascadeType.ALL)
    @OrderBy("uploadDate DESC")
    private List<DocumentVersion> versions = new ArrayList<>();

    public ChecklistDocument() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Provider getProvider() { return provider; }
    public void setProvider(Provider provider) { this.provider = provider; }

    public Worker getWorker() { return worker; }
    public void setWorker(Worker worker) { this.worker = worker; }

    public Machinery getMachinery() { return machinery; }
    public void setMachinery(Machinery machinery) { this.machinery = machinery; }

    public DocumentType getDocumentType() { return documentType; }
    public void setDocumentType(DocumentType documentType) { this.documentType = documentType; }

    public boolean isReceived() { return received; }
    public void setReceived(boolean received) { this.received = received; }

    public LocalDate getExpirationDate() { return expirationDate; }
    public void setExpirationDate(LocalDate expirationDate) { this.expirationDate = expirationDate; }

    public String getFileName() { return fileName; }
    public void setFileName(String fileName) { this.fileName = fileName; }

    public boolean isExempt() { return exempt; }
    public void setExempt(boolean exempt) { this.exempt = exempt; }

    public List<DocumentVersion> getVersions() { return versions; }
    public void setVersions(List<DocumentVersion> versions) { this.versions = versions; }

    public String getStatus() {
        if (exempt) {
            return "EXEMPT";
        }
        if (!received) {
            return "PENDING";
        }
        if (expirationDate != null) {
            LocalDate today = LocalDate.now();
            if (expirationDate.isBefore(today)) {
                return "EXPIRED";
            }
            if (!expirationDate.isAfter(today.plusDays(30))) {
                return "EXPIRING_SOON";
            }
        }
        return "OK";
    }
}