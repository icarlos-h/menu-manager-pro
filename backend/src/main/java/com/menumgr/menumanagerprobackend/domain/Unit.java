package com.menumgr.menumanagerprobackend.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "units")
public class Unit {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 140)
    private String name;

    @Column(nullable = false, length = 40)
    private String phone;

    @Column(nullable = false, length = 120)
    private String instagram;

    @Column(name = "google_maps_url", length = 500)
    private String googleMapsUrl;

    @Column(name = "ifood_url", length = 500)
    private String ifoodUrl;

    @Column(name = "keeta_url", length = 500)
    private String keetaUrl;

    @Column(name = "food99_url", length = 500)
    private String food99Url;

    protected Unit() {}

    public Unit(String name, String phone, String instagram) {
        this.name = name;
        this.phone = phone;
        this.instagram = instagram;
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public String getPhone() { return phone; }
    public String getInstagram() { return instagram; }
    public String getGoogleMapsUrl() { return googleMapsUrl; }
    public String getIfoodUrl() { return ifoodUrl; }
    public String getKeetaUrl() { return keetaUrl; }
    public String getFood99Url() { return food99Url; }

    public void setName(String name) { this.name = name; }
    public void setPhone(String phone) { this.phone = phone; }
    public void setInstagram(String instagram) { this.instagram = instagram; }
    public void setGoogleMapsUrl(String googleMapsUrl) { this.googleMapsUrl = googleMapsUrl; }
    public void setIfoodUrl(String ifoodUrl) { this.ifoodUrl = ifoodUrl; }
    public void setKeetaUrl(String keetaUrl) { this.keetaUrl = keetaUrl; }
    public void setFood99Url(String food99Url) { this.food99Url = food99Url; }
}