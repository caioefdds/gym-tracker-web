/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  jakarta.persistence.Column
 *  jakarta.persistence.Entity
 *  jakarta.persistence.GeneratedValue
 *  jakarta.persistence.GenerationType
 *  jakarta.persistence.Id
 *  jakarta.persistence.Table
 *  lombok.Generated
 */
package com.caiofagundes.gymtracker.user;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.OffsetDateTime;
import lombok.Generated;

@Entity
@Table(name="users")
public class User {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;
    @Column(nullable=false, unique=true)
    private String email;
    @Column(name="password_hash", nullable=false)
    private String passwordHash;
    @Column(nullable=false, length=120)
    private String name;
    @Column(name="created_at", nullable=false, updatable=false, insertable=false)
    private OffsetDateTime createdAt;

    @Generated
    public static UserBuilder builder() {
        return new UserBuilder();
    }

    @Generated
    public Long getId() {
        return this.id;
    }

    @Generated
    public String getEmail() {
        return this.email;
    }

    @Generated
    public String getPasswordHash() {
        return this.passwordHash;
    }

    @Generated
    public String getName() {
        return this.name;
    }

    @Generated
    public OffsetDateTime getCreatedAt() {
        return this.createdAt;
    }

    @Generated
    public void setId(Long id) {
        this.id = id;
    }

    @Generated
    public void setEmail(String email) {
        this.email = email;
    }

    @Generated
    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    @Generated
    public void setName(String name) {
        this.name = name;
    }

    @Generated
    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Generated
    public User() {
    }

    @Generated
    public User(Long id, String email, String passwordHash, String name, OffsetDateTime createdAt) {
        this.id = id;
        this.email = email;
        this.passwordHash = passwordHash;
        this.name = name;
        this.createdAt = createdAt;
    }

    @Generated
    public static class UserBuilder {
        @Generated
        private Long id;
        @Generated
        private String email;
        @Generated
        private String passwordHash;
        @Generated
        private String name;
        @Generated
        private OffsetDateTime createdAt;

        @Generated
        UserBuilder() {
        }

        @Generated
        public UserBuilder id(Long id) {
            this.id = id;
            return this;
        }

        @Generated
        public UserBuilder email(String email) {
            this.email = email;
            return this;
        }

        @Generated
        public UserBuilder passwordHash(String passwordHash) {
            this.passwordHash = passwordHash;
            return this;
        }

        @Generated
        public UserBuilder name(String name) {
            this.name = name;
            return this;
        }

        @Generated
        public UserBuilder createdAt(OffsetDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        @Generated
        public User build() {
            return new User(this.id, this.email, this.passwordHash, this.name, this.createdAt);
        }

        @Generated
        public String toString() {
            return "User.UserBuilder(id=" + this.id + ", email=" + this.email + ", passwordHash=" + this.passwordHash + ", name=" + this.name + ", createdAt=" + String.valueOf(this.createdAt) + ")";
        }
    }
}

