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
import com.mammb.code.jpa.fluent.core.RootSource;
import jakarta.persistence.Tuple;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Selection;
import jakarta.persistence.criteria.Subquery;

import java.util.Arrays;
import java.util.List;

/**
 * Mapper.
 * @param <E> the type of entity
 * @param <R> the type of root
 * @param <U> the type of result
 * @author Naotsugu Kobayashi
 */
public interface Mapper<E, R extends RootAware<E>, U> {

    /**
     * Apply the this {@link Mapper}.
     * @param rootSource the root source
     * @param builder the {@link CriteriaBuilder}
     * @return the {@link RootAware} with mapper applied
     */
    R apply(RootSource<E, R> rootSource, CriteriaBuilder builder);


    /**
     * Mark this Mapper as distinct.
     * @return this Mapper
     */
    Mapper<E, R, U> distinct();


    /**
     * Create a general {@link Mapper}.
     * @param <E> the type of entity
     * @param <R> the type of root
     * @return a general {@link Mapper}
     */
    static <E, R extends RootAware<E>> Mapper<E, R, E> of() {
        return new Mapper<>() {
            private QueryDecorator<E> queryDecorator = QueryDecorator.empty();
            @Override
            public R apply(RootSource<E, R> rootSource, CriteriaBuilder builder) {
                CriteriaQuery<E> query = builder.createQuery(rootSource.rootClass());
                queryDecorator.decorate(query);
                QueryContext.put(query);
                R root = rootSource.root(query.from(rootSource.rootClass()), query, builder);
                query.select(QueryContext.put(root.get()));
                return root;
            }
            @Override
            public Mapper<E, R, E> distinct() {
                queryDecorator = query -> query.distinct(true);
                return this;
            }
        };
    }


    /**
     * Create a {@link Tuple} {@link Mapper}.
     * @param selectors the {@link Tuple} selector.
     * @param grouping the grouping
     * @param <E> the type of entity
     * @param <R> the type of root
     * @return a tuple {@link Mapper}
     */
    static <E, R extends RootAware<E>> Mapper<E, R, Tuple> tuple(
            List<Selector<E, R, ?>> selectors, Grouping<E, R> grouping) {
        return new Mapper<>() {
            private QueryDecorator<Tuple> queryDecorator = QueryDecorator.empty();
            @Override
            public R apply(RootSource<E, R> rootSource, CriteriaBuilder builder) {
                CriteriaQuery<Tuple> query = builder.createTupleQuery();
                queryDecorator.decorate(query);
                QueryContext.put(query);
                R root = rootSource.root(query.from(rootSource.rootClass()), query, builder);
                QueryContext.put(root.get());
                query.select(builder.tuple(selectors.stream()
                    .map(sel -> sel.apply(root)).toArray(Selection[]::new)));
                return root;
            }
            @Override
            public Mapper<E, R, Tuple> distinct() {
                queryDecorator = query -> query.distinct(true);
                return this;
            }
        };
    }


    /**
     * Create a Construct {@link Mapper}.
     * @param result the class of query result
     * @param selectors the selectors
     * @param grouping the grouping
     * @param <E> the type of entity
     * @param <R> the type of root
     * @param <U> the type of result
     * @return a Construct {@link Mapper}
     */
    static <E, R extends RootAware<E>, U> Mapper<E, R, U> construct(
            Class<U> result,
            List<Selector<E, R, ?>> selectors,
            Grouping<E, R> grouping) {
        return new Mapper<>() {
            private QueryDecorator<U> queryDecorator = QueryDecorator.empty();
            @Override
            public R apply(RootSource<E, R> rootSource, CriteriaBuilder builder) {
                CriteriaQuery<U> query = builder.createQuery(result);
                queryDecorator.decorate(query);
                QueryContext.put(query);
                R root = rootSource.root(query.from(rootSource.rootClass()), query, builder);
                QueryContext.put(root.get());
                query.select(builder.construct(result, selectors.stream()
                        .map(sel -> sel.apply(root)).toArray(Selection[]::new)));
                query.groupBy(grouping.apply(root));
                return root;
            }
            @Override
            public Mapper<E, R, U> distinct() {
                queryDecorator = query -> query.distinct(true);
                return this;
            }
        };
    }


    /**
     * Create a Subquery {@link Mapper}.
     * @param <E> the type of entity
     * @param <R> the type of root
     * @return a Subquery {@link Mapper}
     */
    static <E, R extends RootAware<E>> Mapper<E, R, E> subQuery() {
        return new Mapper<>() {
            private QueryDecorator<E> queryDecorator = QueryDecorator.empty();
            @Override
            public R apply(RootSource<E, R> subRootSource, CriteriaBuilder builder) {
                Subquery<E> sq = QueryContext.query().subquery(subRootSource.rootClass());
                queryDecorator.decorate(sq);
                R root = subRootSource.root(sq.from(subRootSource.rootClass()), sq, builder);
                sq.select(root.get());
                return root;
            }
            @Override
            public Mapper<E, R, E> distinct() {
                queryDecorator = query -> query.distinct(true);
                return this;
            }
        };
    }


    /**
     * Create a Subquery {@link Mapper}.
     * @param resultType the class of query result
     * @param selector the selectors
     * @param <E> the type of entity
     * @param <R> the type of root
     * @param <U> the type of result
     * @return a Subquery {@link Mapper}
     */
    static <E, R extends RootAware<E>, U> Mapper<E, R, U> subQuery(
            Class<U> resultType, ExpressionSelector<E, R, U> selector) {
        return new Mapper<>() {
            private QueryDecorator<U> queryDecorator = QueryDecorator.empty();
            @Override
            public R apply(RootSource<E, R> subRootSource, CriteriaBuilder builder) {
                Subquery<U> sq = QueryContext.query().subquery(resultType);
                queryDecorator.decorate(sq);
                R root = subRootSource.root(sq.from(subRootSource.rootClass()), sq, builder);
                sq.select(selector.apply(root));
                return root;
            }
            @Override
            public Mapper<E, R, U> distinct() {
                queryDecorator = query -> query.distinct(true);
                return this;
            }
        };
    }


    /**
     * Create a self correlate Subquery {@link Mapper}.
     * @param <E> the type of entity
     * @param <R> the type of root
     * @return a self correlate Subquery {@link Mapper}
     */
    static <E, R extends RootAware<E>> Mapper<E, R, E> correlate() {
        return new Mapper<>() {
            private QueryDecorator<E> queryDecorator = QueryDecorator.empty();
            @Override
            public R apply(RootSource<E, R> rootSource, CriteriaBuilder builder) {
                Subquery<E> sq = QueryContext.query().subquery(rootSource.rootClass());
                queryDecorator.decorate(sq);
                Root<E> correlate = sq.correlate(QueryContext.root());
                R root = rootSource.root(correlate, sq, builder);
                sq.select(root.get());
                return root;
            }
            @Override
            public Mapper<E, R, E> distinct() {
                queryDecorator = query -> query.distinct(true);
                return this;
            }
        };
    }

}
