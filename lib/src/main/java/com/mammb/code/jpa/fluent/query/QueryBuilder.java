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
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Order;
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
public interface QueryBuilder {

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
        R root = rootSource.root(cq.from(rootSource.rootClass()), cq, cb);
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
     * @param mapper {@link Mapper}
     * @param sorts {@link Sorts}
     * @param <E> the type of entity
     * @param <R> the type of root
     * @param <U> the type of result value
     * @return a query
     */
    static <E, R extends RootAware<E>, U> TypedQuery<U> query(
            EntityManager em,
            RootSource<E, R> rootSource,
            Mapper<E, R, U> mapper,
            Filter<E, R> filter,
            Sorts<E, R> sorts) {

        CriteriaBuilder cb = QueryContext.put(em.getCriteriaBuilder());
        R root = mapper.apply(rootSource, cb);
        @SuppressWarnings("unchecked")
        CriteriaQuery<U> cq = (CriteriaQuery<U>) QueryContext.query();
        Optional.ofNullable(filter.apply(root)).ifPresent(cq::where);

        List<Order> orders = new ArrayList<>();
        Optional.ofNullable(sorts.apply(root)).ifPresent(orders::addAll);
        orders.addAll(getIdentifierName(root.get().getModel()).stream()
            .map(name -> cb.asc(root.get().get(name))).toList());
        cq.orderBy(orders);
        QueryContext.close();
        return em.createQuery(cq);
    }


    /**
     * Create a sub query.
     * @param subRootSource {@link RootSource}
     * @param filter {@link Filter}
     * @param mapper {@link Mapper}
     * @param <E> the type of entity
     * @param <R> the type of root
     * @param <U> the type of result value
     * @return {@link Subquery}
     */
    static <E, R extends RootAware<E>, U> Subquery<U> subQuery(
            RootSource<E, R> subRootSource,
            SubQueryFilter<E, R> filter,
            Mapper<E, R, U> mapper) {
        R root = mapper.apply(subRootSource, QueryContext.builder());
        @SuppressWarnings("unchecked")
        Subquery<U> subQuery = (Subquery<U>) root.query();
        Optional.ofNullable(filter.apply(root, subQuery)).ifPresent(subQuery::where);
        return subQuery;
    }


    /**
     * Get the slice of entity.
     * @param em {@link EntityManager}
     * @param rootSource {@link RootSource}
     * @param mapper {@link Mapper}
     * @param filter {@link Filter}
     * @param sorts {@link Sorts}
     * @param slicePoint  {@link SlicePoint}
     * @param <E> the type of entity
     * @param <R> the type of root
     * @param <U> the type of result value
     * @return a slice
     */
    static <E, R extends RootAware<E>, U> Slice<U> slice(
            EntityManager em, RootSource<E, R> rootSource, Mapper<E, R, U> mapper,
            Filter<E, R> filter, Sorts<E, R> sorts, SlicePoint slicePoint) {
        var query = QueryBuilder.query(em, rootSource, mapper, filter, sorts);
        query.setFirstResult(Math.toIntExact(slicePoint.getOffset()));
        query.setMaxResults(slicePoint.getSize() + 1);
        List<U> result = query.getResultList();
        return (result.size() > slicePoint.getSize())
            ? Slice.of(result.subList(0, slicePoint.getSize()), true, slicePoint)
            : Slice.of(result, false, slicePoint);
    }


    /**
     * Get the page of entity.
     * @param em {@link EntityManager}
     * @param rootSource {@link RootSource}
     * @param mapper {@link Mapper}
     * @param filter {@link Filter}
     * @param sorts {@link Sorts}
     * @param slicePoint  {@link SlicePoint}
     * @param <E> the type of entity
     * @param <R> the type of root
     * @param <U> the type of result value
     * @return a page
     */
    static <E, R extends RootAware<E>, U> Page<U> page(
            EntityManager em, RootSource<E, R> rootSource, Mapper<E, R, U> mapper,
            Filter<E, R> filter, Sorts<E, R> sorts, SlicePoint slicePoint) {

        var count = countQuery(em, rootSource, filter).getSingleResult();
        if (count <= slicePoint.getOffset()) {
            return Page.of(List.of(), count, slicePoint);
        }

        var query = QueryBuilder.query(em, rootSource, mapper, filter, sorts);
        query.setFirstResult(Math.toIntExact(slicePoint.getOffset()));
        query.setMaxResults(slicePoint.getSize());
        List<U> result = query.getResultList();
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
