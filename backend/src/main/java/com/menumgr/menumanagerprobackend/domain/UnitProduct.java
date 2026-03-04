package com.menumgr.menumanagerprobackend.domain;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "unit_products", uniqueConstraints = {
        @UniqueConstraint(name = "uk_unit_product", columnNames = {"unit_id", "product_id"})
})
public class UnitProduct {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "unit_id", nullable = false, foreignKey = @ForeignKey(name = "fk_up_unit"))
    private Unit unit;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false, foreignKey = @ForeignKey(name = "fk_up_product"))
    private Product product;

    @Column(nullable = false)
    private boolean active = true;

    @Column(name = "price_override", precision = 19, scale = 2)
    private BigDecimal priceOverride; // null => usa basePrice

    protected UnitProduct() {}

    public UnitProduct(Unit unit, Product product, boolean active, BigDecimal priceOverride) {
        this.unit = unit;
        this.product = product;
        this.active = active;
        this.priceOverride = priceOverride;
    }

    public Long getId() { return id; }
    public Unit getUnit() { return unit; }
    public Product getProduct() { return product; }
    public boolean isActive() { return active; }
    public BigDecimal getPriceOverride() { return priceOverride; }

    public void setActive(boolean active) { this.active = active; }
    public void setPriceOverride(BigDecimal priceOverride) { this.priceOverride = priceOverride; }
}