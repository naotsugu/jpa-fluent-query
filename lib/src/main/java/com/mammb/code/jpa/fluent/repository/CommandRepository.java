package com.mammb.code.jpa.fluent.repository;

import com.mammb.code.jpa.fluent.repository.trait.DeleteTrait;
import com.mammb.code.jpa.fluent.repository.trait.SaveTrait;
import java.io.Serializable;

public interface CommandRepository<PK extends Serializable, E>
    extends SaveTrait<E>, DeleteTrait<E> {
}
