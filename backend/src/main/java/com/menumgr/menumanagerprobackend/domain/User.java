package com.menumgr.menumanagerprobackend.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    private Long unitId; // null para ADMIN

    @Column(nullable = false)
    private boolean active = true;

    protected User() {}

    public User(String email, String password, Role role, Long unitId) {
        this.email = email;
        this.password = password;
        this.role = role;
        this.unitId = unitId;
        this.active = true;
    }

    public Long getId() { return id; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }
    public Role getRole() { return role; }
    public Long getUnitId() { return unitId; }
    public boolean isActive() { return active; }
}