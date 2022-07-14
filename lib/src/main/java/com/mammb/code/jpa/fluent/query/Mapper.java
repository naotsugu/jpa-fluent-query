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
import com.mammb.code.jpa.core.RootSource;
import jakarta.persistence.Tuple;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Selection;
import jakarta.persistence.criteria.Subquery;
import java.util.List;

/**
 * Mapper.
 * @param <E> the type of entity
 * @param <R> the type of root
 * @param <U> the type of result
 * @author Naotsugu Kobayashi
 */
@FunctionalInterface
public interface Mapper<E, R extends RootAware<E>, U> {

    R apply(RootSource<E, R> rootSource, CriteriaBuilder builder);


    static <E, R extends RootAware<E>> Mapper<E, R, E> of() {
        return (RootSource<E, R> rootSource, CriteriaBuilder builder) ->  {
            CriteriaQuery<E> query = builder.createQuery(rootSource.rootClass());
            QueryContext.put(query);
            R root = rootSource.root(query.from(rootSource.rootClass()), query, builder);
            query.select(QueryContext.put(root.get()));
            return root;
        };
    }


    static <E, R extends RootAware<E>> Mapper<E, R, Tuple> tuple(
            List<Selector<E, R, ?>> selectors) {
        return (RootSource<E, R> rootSource, CriteriaBuilder builder) ->  {
            CriteriaQuery<Tuple> query = builder.createTupleQuery();
            QueryContext.put(query);
            R root = rootSource.root(query.from(rootSource.rootClass()), query, builder);
            QueryContext.put(root.get());
            query.select(builder.tuple(selectors.stream()
                .map(sel -> sel.apply(root)).toArray(Selection[]::new)));
            return root;
        };
    }


    static <E, R extends RootAware<E>, U> Mapper<E, R, U> construct(
            Class<U> result, List<Selector<E, R, ?>> selectors) {
        return (RootSource<E, R> rootSource, CriteriaBuilder builder) ->  {
            CriteriaQuery<U> query = builder.createQuery(result);
            QueryContext.put(query);
            R root = rootSource.root(query.from(rootSource.rootClass()), query, builder);
            QueryContext.put(root.get());
            query.select(builder.construct(result, selectors.stream()
                    .map(sel -> sel.apply(root)).toArray(Selection[]::new)));
            return root;
        };
    }


    static <E, R extends RootAware<E>> Mapper<E, R, E> subQuery() {
        return (RootSource<E, R> subRootSource, CriteriaBuilder builder) ->  {
            Subquery<E> sq = QueryContext.query().subquery(subRootSource.rootClass());
            R root = subRootSource.root(sq.from(subRootSource.rootClass()), sq, builder);
            sq.select(root.get());
            return root;
        };
    }


    static <E, R extends RootAware<E>, U> Mapper<E, R, U> subQuery(
            Class<U> resultType, ExpressionSelector<E, R, U> selector) {
        return (RootSource<E, R> subRootSource, CriteriaBuilder builder) ->  {
            Subquery<U> sq = QueryContext.query().subquery(resultType);
            R root = subRootSource.root(sq.from(subRootSource.rootClass()), sq, builder);
            sq.select(selector.apply(root));
            return root;
        };
    }


    static <E, R extends RootAware<E>> Mapper<E, R, E> correlate() {
        return (RootSource<E, R> rootSource, CriteriaBuilder builder) ->  {
            Subquery<E> sq = QueryContext.query().subquery(rootSource.rootClass());
            //Root<E> raw = sq.from(rootSource.rootClass());
            Root<E> correlate = sq.correlate(QueryContext.root());
            R root = rootSource.root(correlate, sq, builder);
            sq.select(root.get());
            return root;
        };
    }


}
