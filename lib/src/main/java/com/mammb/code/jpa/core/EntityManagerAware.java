package com.mammb.code.jpa.core;

import jakarta.persistence.EntityManager;

/**
 * The entity manager aware.
 * @author Naotsugu Kobayashi
 */
public interface EntityManagerAware {

    /**
     * Get the {@link EntityManager}.
     * @return the {@link EntityManager}
     */
    EntityManager em();

}
