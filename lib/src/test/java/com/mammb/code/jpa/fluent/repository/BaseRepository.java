package com.mammb.code.jpa.fluent.repository;

import com.mammb.code.jpa.core.RepositoryTrait;
import java.io.Serializable;

@RepositoryTrait
public interface BaseRepository<PK extends Serializable, E>
    extends CommandRepository<PK, E> {
}
