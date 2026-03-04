package com.menumgr.menumanagerprobackend.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "unit_categories", uniqueConstraints = {
        @UniqueConstraint(name = "uk_unit_category", columnNames = {"unit_id", "category_id"})
})
public class UnitCategory {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "unit_id", nullable = false, foreignKey = @ForeignKey(name = "fk_uc_unit"))
    private Unit unit;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false, foreignKey = @ForeignKey(name = "fk_uc_category"))
    private Category category;

    @Column(nullable = false)
    private boolean active = false;

    protected UnitCategory() {}

    public UnitCategory(Unit unit, Category category, boolean active) {
        this.unit = unit;
        this.category = category;
        this.active = active;
    }

    public Long getId() { return id; }
    public Unit getUnit() { return unit; }
    public Category getCategory() { return category; }
    public boolean isActive() { return active; }

    public void setActive(boolean active) { this.active = active; }
}