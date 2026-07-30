package com.otto616.onTrack.models;

import jakarta.persistence.*;

@Entity
public class Client {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(unique = true)
    private String taxId;

    @Enumerated(EnumType.STRING)
    private Enums.ClientType clientType;

    public Client() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getTaxId() { return taxId; }
    public void setTaxId(String taxId) { this.taxId = taxId; }

    public Enums.ClientType getClientType() { return clientType; }
    public void setClientType(Enums.ClientType clientType) { this.clientType = clientType; }
}
