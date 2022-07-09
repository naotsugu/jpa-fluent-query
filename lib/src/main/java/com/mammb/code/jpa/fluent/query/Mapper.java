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

import com.mammb.code.jpa.core.QueryContext;
import com.mammb.code.jpa.core.RootAware;
import com.mammb.code.jpa.core.RootSource;
import jakarta.persistence.Tuple;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Selection;
import jakarta.persistence.criteria.Subquery;
import java.util.List;


public interface Mapper<E, R extends RootAware<E>, U> {

    R apply(RootSource<E, R> rootSource, CriteriaBuilder builder);


    static <E, R extends RootAware<E>> Mapper<E, R, E> of() {
        return (RootSource<E, R> rootSource, CriteriaBuilder builder) ->  {
            CriteriaQuery<E> query = QueryContext.put(builder.createQuery(rootSource.rootClass()));
            R root = rootSource.root(query, builder);
            query.select(QueryContext.put(root.get()));
            return root;
        };
    }


    static <E, R extends RootAware<E>> Mapper<E, R, Tuple> tuple(
            List<Selector<E, R, ?>> selectors) {
        return (RootSource<E, R> rootSource, CriteriaBuilder builder) ->  {
            CriteriaQuery<Tuple> query = QueryContext.put(builder.createTupleQuery());
            R root = rootSource.root(query, builder);
            QueryContext.put(root.get());
            query.select(builder.tuple(selectors.stream()
                .map(sel -> sel.apply(root)).toArray(Selection[]::new)));
            return root;
        };
    }


    static <E, R extends RootAware<E>, U> Mapper<E, R, U> construct(
            Class<U> result, List<Selector<E, R, ?>> selectors) {
        return (RootSource<E, R> rootSource, CriteriaBuilder builder) ->  {
            CriteriaQuery<U> query = QueryContext.put(builder.createQuery(result));
            R root = rootSource.root(query, builder);
            QueryContext.put(root.get());
            query.select(builder.construct(result, selectors.stream()
                    .map(sel -> sel.apply(root)).toArray(Selection[]::new)));
            return root;
        };
    }

    static <E, R extends RootAware<E>> Mapper<E, R, E> subQuery() {
        return (RootSource<E, R> rootSource, CriteriaBuilder builder) ->  {
            Subquery<E> sq = QueryContext.query().subquery(rootSource.rootClass());
            R root = rootSource.root(sq, builder);
            sq.select(root.get());
            return root;
        };
    }

    static <E, R extends RootAware<E>, U> Mapper<E, R, U> subQuery(
            Class<U> resultType, ExpressionSelector<E, R, U> selector) {
        return (RootSource<E, R> rootSource, CriteriaBuilder builder) ->  {
            Subquery<U> sq = QueryContext.query().subquery(resultType);
            R root = rootSource.root(sq, builder);
            sq.select(selector.apply(root));
            return root;
        };
    }


}
