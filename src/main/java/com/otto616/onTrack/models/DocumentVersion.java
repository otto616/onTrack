package com.otto616.onTrack.models;

import jakarta.persistence.*;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDate;

@Entity
public class DocumentVersion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private ChecklistDocument checklistDocument;

    private String fileName;

    private String originalFileName;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate uploadDate;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate expirationDate;

    public DocumentVersion() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public ChecklistDocument getChecklistDocument() { return checklistDocument; }
    public void setChecklistDocument(ChecklistDocument checklistDocument) { this.checklistDocument = checklistDocument; }

    public String getFileName() { return fileName; }
    public void setFileName(String fileName) { this.fileName = fileName; }

    public String getOriginalFileName() { return originalFileName; }
    public void setOriginalFileName(String originalFileName) { this.originalFileName = originalFileName; }

    public LocalDate getUploadDate() { return uploadDate; }
    public void setUploadDate(LocalDate uploadDate) { this.uploadDate = uploadDate; }

    public LocalDate getExpirationDate() { return expirationDate; }
    public void setExpirationDate(LocalDate expirationDate) { this.expirationDate = expirationDate; }
}