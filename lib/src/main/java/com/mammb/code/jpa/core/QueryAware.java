package com.mammb.code.jpa.core;

import jakarta.persistence.criteria.AbstractQuery;

public interface QueryAware<Q extends AbstractQuery<?>> {
    Q query();
}
