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
import jakarta.persistence.criteria.Predicate;

/**
 * The filter for WHERE clause.
 * @param <E1> the type of entity
 * @param <R1> the type of root
 * @param <E2>
 * @param <R2>
 * @author Naotsugu Kobayashi
 */
@FunctionalInterface
public interface BiFilter<E1, R1 extends RootAware<E1>, E2, R2 extends RootAware<E2>> {

    /**
     * Creates a {@link Predicate} for the given {@link RootAware}.
     * @param root1 a {@link RootAware}
     * @param root2 a {@link RootAware}
     * @return a {@link Predicate}
     */
    Predicate apply(R1 root1, R2 root2);

}


