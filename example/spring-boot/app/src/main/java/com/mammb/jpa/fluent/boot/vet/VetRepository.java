package com.mammb.jpa.fluent.boot.vet;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public class VetRepository implements VetRepository_ {

    @PersistenceContext
    private EntityManager em;

    @Override
    public EntityManager em() {
        return em;
    }

    public Vet save(Vet entity) {
        return VetRepository_.super.save(entity);
    }

}
