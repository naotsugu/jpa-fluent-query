/*
 * Copyright 2019-2022 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.mammb.code.jpa.entity;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Version;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Abstract base entity.
 * @param <PK> type of primary key
 * @author Naotsugu Kobayashi
 */
@MappedSuperclass
public abstract class BaseEntity<PK extends Serializable> implements Identifiable<PK>, Serializable {

    /** Version. */
    @Version
    @Column(nullable = false)
    private Long version;

    /** createdOn. */
    private LocalDateTime createdOn;

    /** lastModifiedOn. */
    private LocalDateTime lastModifiedOn;

    /**
     * Default constructor.
     */
    protected BaseEntity() { }


    /**
     * Pre persist.
     */
    @PrePersist
    public void prePersistBase() {
        createdOn = LocalDateTime.now();
        lastModifiedOn = createdOn;
    }


    /**
     * Pre update.
     */
    @PreUpdate
    public void preUpdateBase() {
        lastModifiedOn = LocalDateTime.now();
    }


    /**
     * Get the id.
     * @return id
     */
    @Override
    abstract public PK getId();


    /**
     * Get created on.
     * @return created on
     */
    public LocalDateTime getCreatedOn() {
        return createdOn;
    }


    /**
     * Get last modified on.
     * @return last modified on
     */
    public LocalDateTime getLastModifiedOn() {
        return lastModifiedOn;
    }


    /**
     * Get version.
     * @return version
     */
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
