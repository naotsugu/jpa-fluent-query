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
import com.mammb.code.jpa.core.QueryContext;
import com.mammb.code.jpa.core.RootAware;
import com.mammb.code.jpa.core.RootSource;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Predicate;

/**
 * SubQuerying.
 * @param <E> the type of entity
 * @param <R> the type of root
 * @param <U> the type of query result
 * @author Naotsugu Kobayashi
 */
public interface SubQuery<E, R extends RootAware<E>, U> {

    SubQuery<E, R, U> filter(Filter<E, R> filter);

    <E1, R1 extends RootAware<E1>> SubQuery<E, R, U> filter(R1 correlateRoot, BiFilter<E1, R1, E, R> filter);

    RootSource<E, R> rootSource();
    Mapper<E, R, U> mapper();
    SubQueryFilter<E, R> filter();

    default <U> Expression<U> to(Class<U> resultType, Criteria.ExpressionSelector<E, R, U> selector) {
        return QueryBuilder.subQuery(rootSource(), filter(),
            Mapper.subQuery(resultType, ExpressionSelector.of(selector)));
    }

    default Expression<Long> count() {
        return QueryContext.builder().count(QueryBuilder.subQuery(rootSource(), filter(), mapper()));
    }

    default Predicate exists() {
        return QueryContext.builder().exists(QueryBuilder.subQuery(rootSource(), filter(), mapper()));
    }

    static <E, R extends RootAware<E>> SubQuery<E, R, E> of(RootSource<E, R> subRootSource) {
        return SubQuery.of(subRootSource, Mapper.subQuery(), SubQueryFilter.empty());
    }

    static <E, R extends RootAware<E>> SubQuery<E, R, E> of(R correlate) {
        return SubQuery.of(RootSource.directly(correlate, correlate.type()),
            Mapper.subQuery(), SubQueryFilter.empty());
    }

    private static <E, R extends RootAware<E>, U> SubQuery<E, R, U> of(
            RootSource<E, R> subRootSource, Mapper<E, R, U> mapper, SubQueryFilter<E, R> filter) {

        return new SubQuery<>() {
            @Override
            public SubQuery<E, R, U> filter(Filter<E, R> f) {
                return SubQuery.of(rootSource(), mapper(), filter().and(SubQueryFilter.of(f)));
            }
            @Override
            public <E1, R1 extends RootAware<E1>> SubQuery<E, R, U> filter(
                    R1 correlateRoot, BiFilter<E1, R1, E, R> filter) {
                return SubQuery.of(rootSource(), mapper(),
                    filter().and(SubQueryFilter.correlateOf(QueryContext.root(), correlateRoot, filter)));
            }
            @Override
            public SubQueryFilter<E, R> filter() { return filter; }
            @Override
            public RootSource<E, R> rootSource() { return subRootSource; }
            @Override
            public Mapper<E, R, U> mapper() { return mapper; }
        };

    }

}
