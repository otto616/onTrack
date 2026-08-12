package com.otto616.onTrack.models;

import jakarta.persistence.*;

@Entity
public class Worker {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String dni;
    private String phone;
    private String socialSecurity;

    @ManyToOne
    private Provider provider;

    public Worker() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDni() { return dni; }
    public void setDni(String dni) { this.dni = dni; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getSocialSecurity() { return socialSecurity; }
    public void setSocialSecurity(String socialSecurity) { this.socialSecurity = socialSecurity; }

    public Provider getProvider() { return provider; }
    public void setProvider(Provider provider) { this.provider = provider; }
}
