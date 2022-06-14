package com.mammb.code.jpa.fluent.repository.trait;

import com.mammb.code.jpa.core.EntityManagerAware;

public interface DeleteTrait<E> extends EntityManagerAware {

    default void delete(E entity) {
        em().remove(entity);
    }

}