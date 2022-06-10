package com.mammb.code.jpa.fluent.query.impl;

import com.mammb.code.jpa.core.RootAware;
import com.mammb.code.jpa.core.RootSource;
import com.mammb.code.jpa.fluent.query.Filter;
import com.mammb.code.jpa.fluent.query.Query;
import com.mammb.code.jpa.fluent.query.Querying;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

public class QueryingImpl<E, R extends RootAware<E>> implements Querying<E, R> {

    private final RootSource<E, R> roots;
    private final Filter<E, R> filter;

    QueryingImpl(RootSource<E, R> roots, Filter<E, R> filter) {
        this.roots = roots;
        this.filter = filter;
    }

    public QueryingImpl(RootSource<E, R> roots) {
        this.roots = roots;
        this.filter = r -> null;
    }

    @Override
    public Querying<E, R> filter(Filter<E, R> filter) {
        return new QueryingImpl<>(roots, this.filter.and(filter));
    }

    @Override
    public Query<List<E>> toList() {
        return em -> {

            CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
            CriteriaQuery<E> criteriaQuery = criteriaBuilder.createQuery(roots.rootClass());

            R r = roots.root(criteriaQuery, criteriaBuilder);

            criteriaQuery.select(r.get());
            Optional.ofNullable(filter.apply(r)).ifPresent(criteriaQuery::where);
            TypedQuery<E> typedQuery = em.createQuery(criteriaQuery);
            //typedQuery.setFirstResult()
            //typedQuery.setMaxResults()
            return typedQuery.getResultList();
        };
    }

}
