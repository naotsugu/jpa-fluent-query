package com.mammb.code.jpa.fluent.repository.trait;

import com.mammb.code.jpa.core.EntityManagerAware;
import java.util.Objects;

public interface Save<E> extends EntityManagerAware {

    default E save(E entity) {
        if (Objects.isNull(em().getEntityManagerFactory().getPersistenceUnitUtil().getIdentifier(entity))) {
            em().persist(entity);
        } else {
            em().merge(entity);
        }
        return entity;
    }

    default E saveAndFlash(E entity) {
        entity = save(entity);
        em().flush();
        return entity;
    }

}
