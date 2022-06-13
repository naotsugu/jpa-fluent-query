package com.mammb.code.jpa.core;

import jakarta.persistence.EntityManager;

public interface EntityManagerAware {
    EntityManager em();
}
