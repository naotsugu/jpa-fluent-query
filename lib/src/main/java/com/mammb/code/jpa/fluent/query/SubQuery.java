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
import com.mammb.code.jpa.fluent.core.RootSource;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Predicate;

/**
 * Represents a subquery.
 * @param <E> the type of entity
 * @param <R> the type of root
 * @param <U> the type of query result
 * @author Naotsugu Kobayashi
 */
public interface SubQuery<E, R extends RootAware<E>, U> {

    /**
     * Apply the given filter to the current {@link SubQuery}.
     * @param filter the filter to apply
     * @return filter applied {@link SubQuery}
     */
    SubQuery<E, R, U> filter(Filter<E, R> filter);


    /**
     * Apply the given filter to a {@link SubQuery} with correlate root.
     * @param correlateRoot the correlate {@link RootAware}
     * @param filter the filter to apply
     * @param <E1> the type of parent entity
     * @param <R1> the type of parent root
     * @return filter applied {@link SubQuery}
     */
    <E1, R1 extends RootAware<E1>> SubQuery<E, R, U> filter(R1 correlateRoot, BiFilter<E1, R1, E, R> filter);


    /**
     * Apply the consisting of the distinct elements to the current {@link Querying}.
     * @return the new {@link Querying} applied distinct
     */
    SubQuery<E, R, U> distinct();


    /**
     * Get the {@link RootSource} of this subquery.
     * @return the {@link RootSource}
     */
    RootSource<E, R> rootSource();


    /**
     * Get the {@link Mapper} of this subquery.
     * @return the {@link Mapper}
     */
    Mapper<E, R, U> mapper();


    /**
     * Get the {@link SubQueryFilter} of this subquery.
     * @return the {@link SubQueryFilter}
     */
    SubQueryFilter<E, R> filter();


    /**
     * Create an expression from this subquery.
     * @param resultType class of subquery result type
     * @param selector the expression selector
     * @param <U> type of subquery result
     * @return an expression
     */
    default <U> Expression<U> to(Class<U> resultType, Criteria.ExpressionSelector<E, R, U> selector) {
        return QueryBuilder.subQuery(rootSource(), filter(),
            Mapper.subQuery(resultType, ExpressionSelector.of(selector)));
    }


    /**
     * Create a count expression from this subquery.
     * @return a count expression
     */
    default Expression<Long> count() {
        return QueryContext.builder().count(QueryBuilder.subQuery(rootSource(), filter(), mapper()));
    }


    /**
     * Create an exists predicate from this subquery.
     * @return an exists predicate
     */
    default Predicate exists() {
        return QueryContext.builder().exists(QueryBuilder.subQuery(rootSource(), filter(), mapper()));
    }


    /**
     * Create a {@link SubQuery} from the given root source.
     * @param subRootSource root source of the subquery
     * @param <E> the type of entity
     * @param <R> the type of root
     * @return a {@link SubQuery}
     */
    static <E, R extends RootAware<E>> SubQuery<E, R, E> of(RootSource<E, R> subRootSource) {
        return SubQuery.of(subRootSource, Mapper.subQuery(), SubQueryFilter.empty());
    }


    /**
     * Create a {@link SubQuery} from the given correlate root.
     * @param correlateRoot the correlate root
     * @param <E> the type of entity
     * @param <R> the type of root
     * @return a {@link SubQuery}
     */
    static <E, R extends RootAware<E>> SubQuery<E, R, E> of(R correlateRoot) {
        return SubQuery.of(RootSource.directly(correlateRoot, correlateRoot.type()),
            Mapper.correlate(), SubQueryFilter.empty());
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
            public SubQuery<E, R, U> distinct() { return SubQuery.of(rootSource(), mapper().distinct(), filter()); }
            @Override
            public SubQueryFilter<E, R> filter() { return filter; }
            @Override
            public RootSource<E, R> rootSource() { return subRootSource; }
            @Override
            public Mapper<E, R, U> mapper() { return mapper; }
        };
    }

}
