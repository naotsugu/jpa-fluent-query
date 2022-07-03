package com.mammb.code.jpa.core;

import jakarta.persistence.criteria.Subquery;

public interface SubRoot<E, U> extends RootAware<E> {
    @Override Subquery<U> query();
}
