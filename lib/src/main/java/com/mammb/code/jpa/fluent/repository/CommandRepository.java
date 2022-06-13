package com.mammb.code.jpa.fluent.repository;

import com.mammb.code.jpa.fluent.repository.trait.Delete;
import com.mammb.code.jpa.fluent.repository.trait.Save;
import java.io.Serializable;

public interface CommandRepository<PK extends Serializable, E>
    extends Repository<PK, E>,
            Save<E>,
            Delete<E> {
}
