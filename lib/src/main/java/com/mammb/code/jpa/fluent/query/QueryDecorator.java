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

import jakarta.persistence.criteria.AbstractQuery;

/**
 * QueryDecorator.
 * @param <U> the type of query result
 */
public interface QueryDecorator<U> {

    /**
     * Decorate for the given query.
     * @param query the query to be decorated
     * @return the decorated query
     */
    AbstractQuery<U> decorate(AbstractQuery<U> query);


    /**
     * Create the empty {@link QueryDecorator}.
     * @param <U> the type of query result
     * @return the empty {@link QueryDecorator}
     */
    static <U> QueryDecorator<U> empty() { return query -> query; }


    /**
     * Create the distinct {@link QueryDecorator}.
     * @param <U> the type of query result
     * @return the distinct {@link QueryDecorator}
     */
    static <U> QueryDecorator<U> distinct() { return query -> query.distinct(true); }

}
