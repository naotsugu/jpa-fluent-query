package com.mammb.code.jpa.fluent.query;

import com.mammb.code.jpa.core.RootSource;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

public interface Querying<E, R extends Supplier<Root<E>>> {

    Querying<E, R> filter(Function<R, Predicate> predicate);

    Query<List<E>> toList();

    static <E, R extends Supplier<Root<E>>> Querying<E, R> of(RootSource<E, R> rootSource) {

        return new Querying<>() {

            final RootSource<E, R> root = rootSource;
            final List<Function<R, Predicate>> predicates = new ArrayList<>();

            @Override
            public Querying<E, R> filter(Function<R, Predicate> predicate) {
                predicates.add(predicate);
                return this;
            }

            @Override
            public Query<List<E>> toList() {
                return em -> {
                    CriteriaBuilder cb = em.getCriteriaBuilder();
                    CriteriaQuery<E> query = cb.createQuery(root.rootClass());
                    R r = root.root(query, cb);
                    query.select(r.get());
                    if (!predicates.isEmpty()) {
                        query.where(predicates.stream().map(p -> p.apply(r)).toArray(Predicate[]::new));
                    }
                    return em.createQuery(query).getResultList();
                };
            }
        };
    }

}
