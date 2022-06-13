package com.mammb.code.jpa.fluent.repository;

import com.mammb.code.jpa.core.RootAware;
import java.io.Serializable;

public interface BaseRepository <PK extends Serializable, E, R extends RootAware<E>>
    extends QueryRepository<PK, E, R>, CommandRepository<PK, E> {
}
