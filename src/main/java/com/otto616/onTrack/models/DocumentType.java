package com.otto616.onTrack.models;

import jakarta.persistence.*;

@Entity
public class DocumentType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Enumerated(EnumType.STRING)
    private Enums.ClientType clientType;

    @Enumerated(EnumType.STRING)
    private Enums.DocumentCategory category;

    private boolean expires;

    public DocumentType() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Enums.ClientType getClientType() { return clientType; }
    public void setClientType(Enums.ClientType clientType) { this.clientType = clientType; }

    public Enums.DocumentCategory getCategory() { return category; }
    public void setCategory(Enums.DocumentCategory category) { this.category = category; }

    public boolean isExpires() { return expires; }
    public void setExpires(boolean expires) { this.expires = expires; }
}