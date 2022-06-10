package com.mammb.code.jpa.core;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import java.util.function.Supplier;

public interface RootSource<E, T extends RootAware<E>> {
    T root(CriteriaQuery<?> query, CriteriaBuilder builder);
    Class<E> rootClass();
}
