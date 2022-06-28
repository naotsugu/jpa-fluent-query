/*
 * Copyright 2019-2022 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.mammb.code.jpa.fluent.query;

import com.mammb.code.jpa.core.RootAware;
import com.mammb.code.jpa.core.RootSource;
import com.mammb.code.jpa.core.SubRootSource;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Tuple;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.AbstractQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Order;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Selection;
import jakarta.persistence.criteria.Subquery;
import jakarta.persistence.metamodel.EntityType;
import jakarta.persistence.metamodel.SingularAttribute;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Helper for building query.
 *
 * @author Naotsugu Kobayashi
 */
public interface QueryHelper {

    /**
     * Create a count query.
     * @param em {@link EntityManager}
     * @param rootSource {@link RootSource}
     * @param filter {@link Filter}
     * @param <E> the type of entity
     * @param <R> the type of root
     * @return a count query
     */
    static <E, R extends RootAware<E>> TypedQuery<Long> countQuery(
            EntityManager em, RootSource<E, R> rootSource, Filter<E, R> filter) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        R root = rootSource.root(cq, cb);
        cq.select(cq.isDistinct() ? cb.countDistinct(root.get()) : cb.count(root.get()));
        Optional.ofNullable(filter.apply(root)).ifPresent(cq::where);
        cq.orderBy(List.of());
        return em.createQuery(cq);
    }


    /**
     * Create a query.
     * @param em {@link EntityManager}
     * @param rootSource {@link RootSource}
     * @param filter {@link Filter}
     * @param sorts {@link Sorts}
     * @param <E> the type of entity
     * @param <R> the type of root
     * @return a query
     */
    static <E, R extends RootAware<E>> TypedQuery<E> query(
            EntityManager em,
            RootSource<E, R> rootSource,
            Filter<E, R> filter,
            Sorts<E, R> sorts) {

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<E> cq = cb.createQuery(rootSource.rootClass());
        R root = rootSource.root(cq, cb);
        cq.select(root.get());
        Optional.ofNullable(filter.apply(root)).ifPresent(cq::where);

        List<Order> orders = new ArrayList<>();
        Optional.ofNullable(sorts.apply(root)).ifPresent(orders::addAll);
        orders.addAll(getIdentifierName(root.get().getModel()).stream()
            .map(name -> cb.asc(root.get().get(name))).toList());
        cq.orderBy(orders);

        return em.createQuery(cq);
    }


    /**
     * Create a tuple query.
     * @param em {@link EntityManager}
     * @param rootSource {@link RootSource}
     * @param filter {@link Filter}
     * @param sorts {@link Sorts}
     * @param <E> the type of entity
     * @param <R> the type of root
     * @return a tuple query
     */
    static <E, R extends RootAware<E>> TypedQuery<Tuple> tupleQuery(
        EntityManager em, RootSource<E, R> rootSource, Filter<E, R> filter, Sorts<E, R> sorts,
        Selection<?>... selections) {

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Tuple> cq = cb.createTupleQuery();
        cq.select(cb.tuple(selections));
        R root = rootSource.root(cq, cb);
        Optional.ofNullable(filter.apply(root)).ifPresent(cq::where);

        List<Order> orders = new ArrayList<>();
        Optional.ofNullable(sorts.apply(root)).ifPresent(orders::addAll);
        orders.addAll(getIdentifierName(root.get().getModel()).stream()
            .map(name -> cb.asc(root.get().get(name))).toList());
        cq.orderBy(orders);

        return em.createQuery(cq);
    }

    @SuppressWarnings("unchecked")
    static <E, R extends RootAware<E>, U> Subquery<U> subQuery(
            AbstractQuery<?> query, CriteriaBuilder cb,
            SubRootSource<E, R, U> subRootSource, Filter<E, R> filter) {
        R subRoot = subRootSource.root(query, cb);
        AbstractQuery<?> abstractQuery = subRoot.query();
        if (abstractQuery instanceof Subquery sq) {
            Optional.ofNullable(filter.apply(subRoot)).ifPresent(sq::where);
            return (Subquery<U>) sq;
        } else {
            throw new RuntimeException("");
        }
    }



    /**
     * Get the slice of entity.
     * @param em {@link EntityManager}
     * @param rootSource {@link RootSource}
     * @param filter {@link Filter}
     * @param sorts {@link Sorts}
     * @param slicePoint  {@link SlicePoint}
     * @param <E> the type of entity
     * @param <R> the type of root
     * @return a slice
     */
    static <E, R extends RootAware<E>> Slice<E> slice(
            EntityManager em, RootSource<E, R> rootSource, Filter<E, R> filter, Sorts<E, R> sorts, SlicePoint slicePoint) {
        var query = QueryHelper.query(em, rootSource, filter, sorts);
        query.setFirstResult(Math.toIntExact(slicePoint.getOffset()));
        query.setMaxResults(slicePoint.getSize() + 1);
        List<E> result = query.getResultList();
        return (result.size() > slicePoint.getSize())
            ? Slice.of(result.subList(0, slicePoint.getSize()), true, slicePoint)
            : Slice.of(result, false, slicePoint);
    }


    /**
     * Get the page of entity.
     * @param em {@link EntityManager}
     * @param rootSource {@link RootSource}
     * @param filter {@link Filter}
     * @param sorts {@link Sorts}
     * @param slicePoint  {@link SlicePoint}
     * @param <E> the type of entity
     * @param <R> the type of root
     * @return a page
     */
    static <E, R extends RootAware<E>> Page<E> page(
            EntityManager em, RootSource<E, R> rootSource, Filter<E, R> filter, Sorts<E, R> sorts, SlicePoint slicePoint) {

        var count = countQuery(em, rootSource, filter).getSingleResult();
        if (count <= slicePoint.getOffset()) {
            return Page.of(List.of(), count, slicePoint);
        }

        var query = QueryHelper.query(em, rootSource, filter, sorts);
        query.setFirstResult(Math.toIntExact(slicePoint.getOffset()));
        query.setMaxResults(slicePoint.getSize());
        List<E> result = query.getResultList();
        return Page.of(result, count, slicePoint);
    }


    /**
     * Get the entity id property names.
     * @param entityType the entity type
     * @return the entity id property names
     */
    private static List<String> getIdentifierName(EntityType<?> entityType) {
        return entityType.hasSingleIdAttribute()
            ? List.of(entityType.getId(entityType.getIdType().getJavaType()).getName())
            : entityType.getIdClassAttributes().stream()
                .map(SingularAttribute::getName)
                .toList();
    }

}
