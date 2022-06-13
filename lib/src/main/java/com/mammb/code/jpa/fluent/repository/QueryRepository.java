package com.mammb.code.jpa.fluent.repository;

import com.mammb.code.jpa.core.RootAware;
import com.mammb.code.jpa.fluent.repository.trait.FindAll;
import com.mammb.code.jpa.fluent.repository.trait.FindPage;
import com.mammb.code.jpa.fluent.repository.trait.FindSlice;
import com.mammb.code.jpa.fluent.repository.trait.GetById;

import java.io.Serializable;

public interface QueryRepository<PK extends Serializable, E, R extends RootAware<E>>
    extends Repository<PK, E>,
            GetById<PK, E, R>,
            FindAll<E, R>,
            FindSlice<E, R>,
            FindPage<E, R> {
}
