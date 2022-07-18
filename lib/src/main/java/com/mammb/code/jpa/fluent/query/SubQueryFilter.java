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

import com.mammb.code.jpa.fluent.core.RootAware;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import java.io.Serializable;
import java.util.Objects;

/**
 * Filter interface for subquery.
 * Representation a Where clause to be applied to a subquery.
 * @param <E> the type of entity
 * @param <R> the type of root
 * @author Naotsugu Kobayashi
 */
@FunctionalInterface
interface SubQueryFilter<E, R extends RootAware<E>> {

    /**
     * Creates a {@link Predicate} for the given {@link RootAware} and {@link Subquery}.
     * @param root a {@link RootAware}
     * @param subquery a {@link Subquery}
     * @return a {@link Predicate}
     */
    Predicate apply(R root, Subquery<?> subquery);


    /**
     * Creates a {@link SubQueryFilter} for the given {@link Filter}.
     * @param filter the source of {@link Filter}
     * @param <E> the type of entity
     * @param <R> the type of root
     * @return a {@link SubQueryFilter}
     */
    static <E, R extends RootAware<E>> SubQueryFilter<E, R> of(Filter<E, R> filter) {
        return (root, subquery) -> filter.apply(root);
    }


    /**
     * Creates a correlate {@link SubQueryFilter} from the given conditions.
     * @param root the raw root
     * @param correlateRoot the root for correlate
     * @param biFilter the {@link BiFilter}
     * @param <E1> the type of another entity
     * @param <R1> the type of another root
     * @param <E> the type of self entity
     * @param <R> the type of self root
     * @return a {@link SubQueryFilter}
     */
    static <E1, R1 extends RootAware<E1>, E, R extends RootAware<E>> SubQueryFilter<E, R> correlateOf(
            Root<E1> root, R1 correlateRoot, BiFilter<E1, R1, E, R> biFilter) {
        return (R rootAware2, Subquery<?> subquery) -> {
                Root<E1> correlate = subquery.correlate(root);
                @SuppressWarnings("unchecked")
                R1 rootAware1 = (R1) correlateRoot.with(correlate, subquery);
                return biFilter.apply(rootAware1, rootAware2);
        };
    }


    /**
     * Create the empty {@link SubQueryFilter}.
     * @param <E> the type of entity
     * @param <R> the type of root
     * @return a empty {@link SubQueryFilter}
     */
    static <E, R extends RootAware<E>> SubQueryFilter<E, R> empty() {
        return (root, subquery) -> null;
    }


    /**
     * ANDs the given {@link SubQueryFilter} to the current one.
     * @param other a {@link SubQueryFilter} for AND target
     * @return a {@link SubQueryFilter}
     */
    default SubQueryFilter<E, R> and(SubQueryFilter<E, R> other) {
        return SubQueryFilter.Composition.composed(this, other, CriteriaBuilder::and);
    }


    /**
     * ORs the given {@link SubQueryFilter} to the current one.
     * @param other a {@link SubQueryFilter} for OR target
     * @return a {@link SubQueryFilter}
     */
    default SubQueryFilter<E, R> or(SubQueryFilter<E, R> other) {
        return SubQueryFilter.Composition.composed(this, other, CriteriaBuilder::or);
    }


    /**
     * The {@link Filter} composition helper.
     */
    class Composition {

        interface Combiner extends Serializable {
            Predicate combine(CriteriaBuilder builder, Predicate lhs, Predicate rhs);
        }

        static <E, R extends RootAware<E>> SubQueryFilter<E, R> composed(
            SubQueryFilter<E, R> lhs, SubQueryFilter<E, R> rhs, SubQueryFilter.Composition.Combiner combiner) {

            return (root, subquery) -> {

                Predicate that  = Objects.isNull(lhs) ? null : lhs.apply(root, subquery);
                Predicate other = Objects.isNull(rhs) ? null : rhs.apply(root, subquery);

                if (Objects.isNull(that)) {
                    return other;
                }

                return Objects.isNull(other)
                    ? that
                    : combiner.combine(root.builder(), that, other);
            };
        }
    }

}

