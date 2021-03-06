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

import com.mammb.code.jpa.fluent.core.Criteria;
import com.mammb.code.jpa.fluent.core.RootAware;
import jakarta.persistence.criteria.Expression;

/**
 * ExpressionSelector.
 * @param <E> the type of entity
 * @param <R> the type of root element
 * @param <U> the type of query result
 * @author Naotsugu Kobayashi
 */
@FunctionalInterface
public interface ExpressionSelector<E, R extends RootAware<E>, U> {

    /**
     * Apply the this {@link ExpressionSelector}.
     * @param root the {@link RootAware}
     * @return the {@link Expression} with {@link ExpressionSelector} applied
     */
    Expression<U> apply(R root);


    /**
     * Create an {@link ExpressionSelector} for given source.
     * @param source the source of {@link ExpressionSelector}
     * @param <E> the type of entity
     * @param <R> the type of root element
     * @param <U> the type of query result
     * @return an {@link ExpressionSelector}
     */
    static <E, R extends RootAware<E>, U> ExpressionSelector<E, R, U> of(
            Criteria.ExpressionSelector<E, R, U> source) {
        return r -> source.apply(r).get();
    }

}
