package com.mammb.jpa.fluent.boot.system;

import com.mammb.code.jpa.fluent.core.RepositoryTrait;
import com.mammb.code.jpa.fluent.core.RootAware;
import com.mammb.code.jpa.fluent.repository.CommandTrait;
import com.mammb.code.jpa.fluent.repository.QueryTrait;
import java.io.Serializable;

@RepositoryTrait
public interface BaseRepository<PK extends Serializable, E, R extends RootAware<E>>
        extends QueryTrait<PK, E, R>, CommandTrait<PK, E> {
}

