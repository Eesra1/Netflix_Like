package com.jstream.model;

import java.time.LocalDateTime;

public class User {
    private int id;
    private String name;
    private String email;
    private String passwordHash;
    private String role;

    // ── Abonnement ──────────────────────────────────────────
    private LocalDateTime createdAt;
    private LocalDateTime subStart;
    private LocalDateTime subEnd;
    private String subStatus; // ACTIVE | EXPIRED | INACTIVE

    public User() {}

    public User(int id, String name, String email, String passwordHash, String role) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.passwordHash = passwordHash;
        this.role = role;
    }

    public User(int id, String name, String email, String passwordHash, String role,
                LocalDateTime createdAt, LocalDateTime subStart, LocalDateTime subEnd, String subStatus) {
        this.id           = id;
        this.name         = name;
        this.email        = email;
        this.passwordHash = passwordHash;
        this.role         = role;
        this.createdAt    = createdAt;
        this.subStart     = subStart;
        this.subEnd       = subEnd;
        this.subStatus    = subStatus;
    }

    // ── Getters / Setters ────────────────────────────────────
    public int getId()                        { return id; }
    public void setId(int id)                 { this.id = id; }

    public String getName()                   { return name; }
    public void setName(String name)          { this.name = name; }

    public String getEmail()                  { return email; }
    public void setEmail(String email)        { this.email = email; }

    public String getPasswordHash()           { return passwordHash; }
    public void setPasswordHash(String h)     { this.passwordHash = h; }

    public String getRole()                   { return role; }
    public void setRole(String role)          { this.role = role; }

    public LocalDateTime getCreatedAt()       { return createdAt; }
    public void setCreatedAt(LocalDateTime d) { this.createdAt = d; }

    public LocalDateTime getSubStart()        { return subStart; }
    public void setSubStart(LocalDateTime d)  { this.subStart = d; }

    public LocalDateTime getSubEnd()          { return subEnd; }
    public void setSubEnd(LocalDateTime d)    { this.subEnd = d; }

    public String getSubStatus()              { return subStatus; }
    public void setSubStatus(String s)        { this.subStatus = s; }
}
 
