package com.mammb.code.jpa.fluent.repository;

import com.mammb.code.jpa.core.RootAware;
import com.mammb.code.jpa.fluent.query.Filter;
import com.mammb.code.jpa.fluent.query.Sorts;
import com.mammb.code.jpa.fluent.repository.trait.FindAllTrait;
import com.mammb.code.jpa.fluent.repository.trait.FindPageTrait;
import com.mammb.code.jpa.fluent.repository.trait.FindSliceTrait;
import com.mammb.code.jpa.fluent.repository.trait.GetTrait;

import java.io.Serializable;

public interface QueryRepository<PK extends Serializable, E, R extends RootAware<E>>
    extends GetTrait<PK, E, R>,
            FindAllTrait<E, R>,
            FindSliceTrait<E, R>,
            FindPageTrait<E, R> {

    default Filter<E, R> filter() { return Filter.empty(); }

    default Sorts<E, R> sort() { return Sorts.empty(); }

}
