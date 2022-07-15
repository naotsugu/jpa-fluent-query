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

import com.mammb.code.jpa.core.Criteria;
import com.mammb.code.jpa.core.RootAware;
import jakarta.persistence.criteria.Selection;

import java.util.function.Supplier;

/**
 * The selector for SELECT clause.
 *
 * @param <E> the type of entity
 * @param <R> the type of root
 * @param <U> the type of selector result
 * @author Naotsugu Kobayashi
 */
@FunctionalInterface
public interface Selector<E, R extends RootAware<E>, U> {

    /**
     * Apply the given {@link RootAware} to.
     * @param root a {@link RootAware}
     * @return a selector result
     */
    Selection<? extends U> apply(R root);


    /**
     * Create a {@link Selector} from the given source.
     * @param source the source of selector
     * @param <E> the type of entity
     * @param <R> the type of root
     * @param <U> the type of result
     * @return a {@link Selector}
     */
    static <E, R extends RootAware<E>, U> Selector<E, R, U> of(Criteria.Selector<E, R, U> source) {
        return r -> source.apply(r).get();
    }


    /**
     * Get the empty {@link Selector}.
     * @param <E> the type of entity
     * @param <R> the type of root
     * @param <U> the type of sub query result
     * @return the empty {@link Selector}
     */
    static <E, R extends RootAware<E>, U> Selector<E, R, U> empty() {
        return root -> null;
    }


    /**
     * Get the own root {@link Selector}.
     * @param <E> the type of entity
     * @param <R> the type of root
     * @return the identity {@link Selector}
     */
    static <E, R extends RootAware<E>> Selector<E, R, E> identity() { return Supplier::get; }

}
