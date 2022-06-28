package com.mammb.code.jpa.core;

import jakarta.persistence.criteria.AbstractQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Subquery;

/**
 * Sub query root source.
 * @param <E> the type of entity
 * @param <R> the type of root
 * @param <U> the type of sub query result
 * @author Naotsugu Kobayashi
 */
public interface SubRootSource<E, R extends RootAware<E>, U> {

    /**
     * Create the root from the given query.
     * @param query {@link Subquery}
     * @param builder {@link CriteriaBuilder}
     * @return the {@link RootAware}
     */
    R root(AbstractQuery<?> query, CriteriaBuilder builder);


    /**
     * Get the sub query root entity class.
     * @return the sub query root entity class
     */
    Class<E> rootClass();


    /**
     * Get the sub query result type.
     * @return the sub query result type
     */
    Class<U> resultType();

}
