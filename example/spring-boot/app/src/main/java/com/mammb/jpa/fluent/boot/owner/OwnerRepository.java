package com.mammb.jpa.fluent.boot.owner;

import com.mammb.code.jpa.fluent.query.Page;
import com.mammb.code.jpa.fluent.query.Querying;
import com.mammb.code.jpa.fluent.query.SlicePoint;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Repository
public class OwnerRepository implements OwnerRepository_ {

    @PersistenceContext
    private EntityManager em;

    @Override
    public EntityManager em() {
        return em;
    }

    @Transactional(readOnly = true)
    public List<PetType> findPetTypes() {
        return Querying.of(PetTypeModel.root()).toList().on(em());
    }

    @Transactional(readOnly = true)
    public Page<Owner> findByLastName(String lastName, SlicePoint slicePoint) {
        return findPage(slicePoint, e -> e.getLastName().likePartial(lastName));
    }

    @Transactional
    public Owner save(Owner entity) {
        return OwnerRepository_.super.save(entity);
    }

}
