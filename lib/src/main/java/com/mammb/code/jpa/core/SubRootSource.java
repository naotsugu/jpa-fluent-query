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
package com.mammb.code.jpa.core;

import jakarta.persistence.criteria.AbstractQuery;
import jakarta.persistence.criteria.CriteriaBuilder;

/**
 * Sub query root source.
 * @param <E> the type of entity
 * @param <R> the type of root
 * @param <U> the type of sub query result
 * @author Naotsugu Kobayashi
 */
public interface SubRootSource<E, R extends SubRoot<E, U>, U> {

    /**
     * Create the sub root from the given query.
     * @param query {@link AbstractQuery}
     * @param builder {@link CriteriaBuilder}
     * @return the {@link RootAware}
     */
    R root(AbstractQuery<?> query, CriteriaBuilder builder);


    /**
     * Get the sub query root entity class.
     * @return the sub query root entity class
     */
    Class<E> rootClass();


    /**
     * Get the sub query result type.
     * @return the sub query result type
     */
    Class<U> resultType();

}
