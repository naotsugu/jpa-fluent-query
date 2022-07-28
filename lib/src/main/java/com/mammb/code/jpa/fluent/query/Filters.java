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
import jakarta.persistence.criteria.Predicate;
import java.util.Arrays;

/**
 * Utility for the {@link Filter}.
 * @author Naotsugu Kobayashi
 */
public interface Filters {

    /**
     * Compose the predicate AND.
     * @param predicates the predicates
     * @return {@link Predicate}
     */
    static Predicate and(Predicate... predicates) {
        return QueryContext.builder().and(predicates);
    }


    /**
     * Compose the predicate OR.
     * @param predicates the predicates
     * @return {@link Predicate}
     */
    static Predicate or(Predicate... predicates) {
        return QueryContext.builder().or(predicates);
    }


    /**
     * Create a negation of the predicate.
     * @param predicate the predicate
     * @return negated {@link Predicate}
     */
    static Predicate not(Predicate predicate) {
        return predicate.not();
    }


    /**
     * Create an and filter by the given {@link Filter}.
     * @param filter1 the {@link Filter}
     * @param filters the {@link Filter}s
     * @param <E> the type of entity
     * @param <R> the type of root
     * @return an and {@link Filter}
     */
    @SafeVarargs
    static <E, R extends RootAware<E>> Filter<E, R> and(Filter<E, R> filter1, Filter<E, R>... filters) {
        return Arrays.stream(filters).reduce(filter1, Filter::and);
    }


    /**
     * Create an or filter by the given {@link Filter}.
     * @param filter1 the {@link Filter}
     * @param filters the {@link Filter}s
     * @param <E> the type of entity
     * @param <R> the type of root
     * @return an or {@link Filter}
     */
    @SafeVarargs
    static <E, R extends RootAware<E>> Filter<E, R> or(Filter<E, R> filter1, Filter<E, R>... filters) {
        return Arrays.stream(filters).reduce(filter1, Filter::or);
    }

}
