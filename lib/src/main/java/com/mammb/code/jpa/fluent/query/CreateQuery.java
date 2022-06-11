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
import jakarta.persistence.OrderBy;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import java.util.List;
import java.util.Optional;

/**
 * Create Query helper interface.
 * @param <E> the entity of root
 * @param <R> the root element
 */
public interface CreateQuery<E, R extends RootAware<E>> {

    RootSource<E, R> rootSource();

    Filter<E, R> filter();

    Sorts<E, R> sorts();


    default Query<Long> count() {
        return em -> {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Long> cq = cb.createQuery(Long.class);
            R root = rootSource().root(cq, cb);
            cq.select(cq.isDistinct() ? cb.countDistinct(root.get()) : cb.count(root.get()));
            Optional.ofNullable(filter().apply(root)).ifPresent(cq::where);
            cq.orderBy(List.of());
            TypedQuery<Long> typedQuery = em.createQuery(cq);
            return typedQuery.getSingleResult();
        };
    }


    default Query<List<E>> toList() {
        return toList(-1);
    }

    default Query<List<E>> toList(int limit) {
        return em -> {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<E> cq = cb.createQuery(rootSource().rootClass());
            R root = rootSource().root(cq, cb);
            cq.select(root.get());
            Optional.ofNullable(filter().apply(root)).ifPresent(cq::where);
            Optional.ofNullable(sorts().apply(root)).ifPresent(cq::orderBy);
            TypedQuery<E> typedQuery = em.createQuery(cq);
            if (limit > 0) {
                typedQuery.setMaxResults(limit);
            }
            return typedQuery.getResultList();
        };
    }

    default Query<Slice<E>> toSlice(SlicePoint slicePoint) {
        return em -> {

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<E> cq = cb.createQuery(rootSource().rootClass());
            R root = rootSource().root(cq, cb);
            cq.select(root.get());
            Optional.ofNullable(filter().apply(root)).ifPresent(cq::where);
            Optional.ofNullable(sorts().apply(root)).ifPresent(cq::orderBy);
            TypedQuery<E> typedQuery = em.createQuery(cq);

            typedQuery.setFirstResult(Math.toIntExact(slicePoint.getOffset()));
            typedQuery.setMaxResults(slicePoint.getSize() + 1);

            List<E> result = typedQuery.getResultList();
            return (result.size() > slicePoint.getSize())
                ? Slice.of(result.subList(0, slicePoint.getSize()), true, slicePoint)
                : Slice.of(result, false, slicePoint);
        };
    }


    default Query<Page<E>> toPage(SlicePoint slicePoint) {
        return em -> {

            Long count = count().on(em);
            if (count <= slicePoint.getOffset()) {
                return Page.of(List.of(), count, slicePoint);
            }

            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<E> cq = cb.createQuery(rootSource().rootClass());
            R root = rootSource().root(cq, cb);
            cq.select(root.get());
            Optional.ofNullable(filter().apply(root)).ifPresent(cq::where);
            Optional.ofNullable(sorts().apply(root)).ifPresent(cq::orderBy);
            TypedQuery<E> typedQuery = em.createQuery(cq);

            typedQuery.setFirstResult(Math.toIntExact(slicePoint.getOffset()));
            typedQuery.setMaxResults(slicePoint.getSize());

            List<E> result = typedQuery.getResultList();
            return Page.of(result, count, slicePoint);
        };
    }

}
