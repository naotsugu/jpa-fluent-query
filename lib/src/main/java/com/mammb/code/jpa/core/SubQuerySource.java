package com.mammb.code.jpa.core;

import jakarta.persistence.criteria.Subquery;

public interface SubQuerySource<E, R extends RootAware<E>, U> {
    Subquery<U> apply(R rootAware);
}
