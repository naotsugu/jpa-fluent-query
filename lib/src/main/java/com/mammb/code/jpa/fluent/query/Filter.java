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
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Objects;

/**
 * The filter for WHERE clause.
 *
 * @param <E> the type of entity
 * @param <R> the type of root
 * @author Naotsugu Kobayashi
 */
@FunctionalInterface
public interface Filter<E, R extends RootAware<E>> {

    /**
     * Creates a {@link Predicate} for the given {@link RootAware}.
     * @param root a {@link RootAware}
     * @return a {@link Predicate}
     */
    Predicate apply(R root);


    /**
     * ANDs the given {@link Filter} to the current one.
     * @param other a {@link Filter} for AND target
     * @return a {@link Filter}
     */
    default Filter<E, R> and(Filter<E, R> other) {
        return Composition.composed(this, other, CriteriaBuilder::and);
    }


    /**
     * ORs the given {@link Filter} to the current one.
     * @param other a {@link Filter} for OR target
     * @return a {@link Filter}
     */
    default Filter<E, R> or(Filter<E, R> other) {
        return Composition.composed(this, other, CriteriaBuilder::or);
    }


    /**
     * Create filter by the given {@link Filter}.
     * @param filter the {@link Filter}
     * @param <E> the type of entity
     * @param <R> the type of root
     * @return a {@link Filter}
     */
    static <E, R extends RootAware<E>> Filter<E, R> of(Filter<E, R> filter) {
        return filter;
    }


    /**
     * Create filter by the given {@link Filter}.
     * @param filter1 the {@link Filter}
     * @param filter2 the {@link Filter}
     * @param <E> the type of entity
     * @param <R> the type of root
     * @return a {@link Filter}
     */
    static <E, R extends RootAware<E>> Filter<E, R> of(Filter<E, R> filter1, Filter<E, R> filter2) {
        return of(filter1).and(filter2);
    }


    /**
     * Create filter by the given {@link Filter}.
     * @param filter1 the {@link Filter}
     * @param filters the {@link Filter}s
     * @param <E> the type of entity
     * @param <R> the type of root
     * @return a {@link Filter}
     */
    @SafeVarargs
    static <E, R extends RootAware<E>> Filter<E, R> of(Filter<E, R> filter1, Filter<E, R>... filters) {
        return Arrays.stream(filters).reduce(filter1, Filter::and);
    }


    /**
     * Get the empty {@link Filter}.
     * @param <E> the type of entity
     * @param <R> the type of root
     * @return a empty {@link Filter}
     */
    static <E, R extends RootAware<E>> Filter<E, R> empty() {
        return root -> null;
    }


    /**
     * The {@link Filter} composition helper.
     */
    class Composition {

        interface Combiner extends Serializable {
            Predicate combine(CriteriaBuilder builder, Predicate lhs, Predicate rhs);
        }

        private Composition() { }

        static <E, R extends RootAware<E>> Filter<E, R> composed(
            Filter<E, R> lhs, Filter<E, R> rhs, Combiner combiner) {

            return root -> {

                Predicate that  = Objects.isNull(lhs) ? null : lhs.apply(root);
                Predicate other = Objects.isNull(rhs) ? null : rhs.apply(root);

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
