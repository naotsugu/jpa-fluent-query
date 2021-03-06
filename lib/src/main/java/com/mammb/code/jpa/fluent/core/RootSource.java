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
package com.mammb.code.jpa.fluent.core;

import jakarta.persistence.criteria.AbstractQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Root;

/**
 * Root source.
 * @param <E> the type of entity
 * @param <R> the type of root
 * @author Naotsugu Kobayashi
 */
public interface RootSource<E, R extends RootAware<E>> {

    /**
     * Create the root from the given query.
     * @param source the Source {@link Root}
     * @param query {@link AbstractQuery}
     * @param builder {@link CriteriaBuilder}
     * @return the {@link RootAware}
     */
    R root(Root<E> source, AbstractQuery<?> query, CriteriaBuilder builder);


    /**
     * Get the root entity class.
     * @return the root entity class
     */
    Class<E> rootClass();


    /**
     * Create a new {@link RootSource} to generate the given {@link RootAware}.
     * @param root the source {@link RootAware}
     * @param rootClass the class of entity type
     * @param <E> the type of entity
     * @param <R> the type of root
     * @return the {@link RootSource} to generate the given root
     */
    static <E, R extends RootAware<E>> RootSource<E, R> directly(R root, Class<E> rootClass) {
        return new RootSource<>() {
            @Override
            @SuppressWarnings("unchecked")
            public R root(Root<E> source, AbstractQuery<?> query, CriteriaBuilder builder) {
                return (R) root.with(source, query);
            }
            @Override
            public Class<E> rootClass() {
                return rootClass;
            }
        };
    }

}
