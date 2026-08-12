package com.otto616.onTrack.models;

import jakarta.persistence.*;

@Entity
public class Machinery {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String serialNumber;
    private String internalCode;

    @ManyToOne
    private Provider provider;

    public Machinery() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getSerialNumber() { return serialNumber; }
    public void setSerialNumber(String serialNumber) { this.serialNumber = serialNumber; }

    public String getInternalCode() { return internalCode; }
    public void setInternalCode(String internalCode) { this.internalCode = internalCode; }

    public Provider getProvider() { return provider; }
    public void setProvider(Provider provider) { this.provider = provider; }
}
