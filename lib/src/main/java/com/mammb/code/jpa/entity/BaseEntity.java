package com.mammb.code.jpa.entity;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Version;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

@MappedSuperclass
public abstract class BaseEntity<PK extends Serializable> implements Identifiable<PK>, Serializable {

    @Version
    @Column(nullable = false)
    private Long version;

    private LocalDateTime createdOn;

    private LocalDateTime lastModifiedOn;


    @PrePersist
    public void prePersistBase() {
        createdOn = LocalDateTime.now();
        lastModifiedOn = createdOn;
    }

    @PreUpdate
    public void preUpdateBase() {
        lastModifiedOn = LocalDateTime.now();
    }

    @Override
    abstract public PK getId();

    public LocalDateTime getCreatedOn() {
        return createdOn;
    }

    public LocalDateTime getLastModifiedOn() {
        return lastModifiedOn;
    }

    public Long getVersion() {
        return version;
    }

    @Override
    public String toString() {
        return String.format("Entity[%s, %s, %s]",
            this.getClass().getName(),
            Objects.isNull(getId()) ? null : getId().toString(),
            getVersion());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BaseEntity<?> that = (BaseEntity<?>) o;
        return Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }

}
