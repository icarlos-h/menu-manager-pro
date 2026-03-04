package com.menumgr.menumanagerprobackend.domain;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "products", uniqueConstraints = {
        @UniqueConstraint(name = "uk_product_name_category", columnNames = {"category_id", "name"})
})
public class Product {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false, foreignKey = @ForeignKey(name = "fk_product_category"))
    private Category category;

    @Column(nullable = false, length = 160)
    private String name;

    @Column(length = 1000)
    private String description;

    @Column(name = "base_price", nullable = false, precision = 19, scale = 2)
    private BigDecimal basePrice;

    @Column(name = "photo_url", length = 600)
    private String photoUrl;

    @Column(name = "weight_grams")
    private Integer weightGrams;

    protected Product() {}

    public Product(Category category, String name, String description, BigDecimal basePrice, String photoUrl, Integer weightGrams) {
        this.category = category;
        this.name = name;
        this.description = description;
        this.basePrice = basePrice;
        this.photoUrl = photoUrl;
        this.weightGrams = weightGrams;
    }

    public Long getId() { return id; }
    public Category getCategory() { return category; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public BigDecimal getBasePrice() { return basePrice; }
    public String getPhotoUrl() { return photoUrl; }
    public Integer getWeightGrams() { return weightGrams; }

    public void setCategory(Category category) { this.category = category; }
    public void setName(String name) { this.name = name; }
    public void setDescription(String description) { this.description = description; }
    public void setBasePrice(BigDecimal basePrice) { this.basePrice = basePrice; }
    public void setPhotoUrl(String photoUrl) { this.photoUrl = photoUrl; }
    public void setWeightGrams(Integer weightGrams) { this.weightGrams = weightGrams; }
}