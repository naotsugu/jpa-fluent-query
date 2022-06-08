package com.mammb.code.jpa.fluent.query;

import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;

import java.util.function.Function;

/**
 * Executable query.
 *
 * @param <R> the type of querying result
 * @author Naotsugu Kobayashi
 */
public interface Query<R> {

    /**
     * Run query with EntityManager.
     * @param em  EntityManager
     * @return result of query
     */
    R on(EntityManager em);

}
