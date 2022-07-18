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
import jakarta.persistence.criteria.Order;

/**
 * Sort for ORDER BY clause.
 * @param <E> the type of entity
 * @param <R> the type of root
 * @author Naotsugu Kobayashi
 */
@FunctionalInterface
public interface Sort<E, R extends RootAware<E>> {

    /**
     * Creates a {@link Order} for the given {@link RootAware}.
     * @param root a {@link RootAware}
     * @return a {@link Order}
     */
    Order apply(R root);


    /**
     * ANDs the given {@link Sort} to the current one.
     * @param other a {@link Sort} for AND target
     * @return a {@link Sorts}
     */
    default Sorts<E, R> and(Sort<E, R> other) {
        return Sorts.of(this, other);
    }

}
