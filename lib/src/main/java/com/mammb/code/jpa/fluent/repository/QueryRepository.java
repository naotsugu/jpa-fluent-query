package com.mammb.code.jpa.fluent.repository;

import com.mammb.code.jpa.core.RootAware;
import com.mammb.code.jpa.fluent.repository.trait.FindAllTrait;
import com.mammb.code.jpa.fluent.repository.trait.FindPageTrait;
import com.mammb.code.jpa.fluent.repository.trait.FindSliceTrait;
import com.mammb.code.jpa.fluent.repository.trait.GetTrait;

import java.io.Serializable;

public interface QueryRepository<PK extends Serializable, E, R extends RootAware<E>>
    extends Repository<PK, E>,
            GetTrait<PK, E, R>,
            FindAllTrait<E, R>,
            FindSliceTrait<E, R>,
            FindPageTrait<E, R> {
}
