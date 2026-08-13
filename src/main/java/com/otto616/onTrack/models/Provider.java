package com.otto616.onTrack.models;

import jakarta.persistence.*;

@Entity
public class Provider {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(unique = true)
    private String taxId;

    @Enumerated(EnumType.STRING)
    private Enums.ProviderType providerType;

    @Column(nullable = false, columnDefinition = "boolean default true")
    private boolean isActive = true;

    public Provider() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getTaxId() { return taxId; }
    public void setTaxId(String taxId) { this.taxId = taxId; }

    public Enums.ProviderType getProviderType() { return providerType; }
    public void setProviderType(Enums.ProviderType providerType) { this.providerType = providerType; }

    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }
}