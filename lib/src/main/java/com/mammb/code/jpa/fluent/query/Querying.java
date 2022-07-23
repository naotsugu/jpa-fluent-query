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

/**
 * Represents a query that should be executed.
 * @param <E> the type of entity
 * @param <R> the type of root
 * @author Naotsugu Kobayashi
 */
public interface Querying<E, R extends RootAware<E>, U> extends CreateQuery<E, R, U> {

    /**
     * Apply the given {@link Filter} to the current {@link Querying}.
     * @param filter the {@link Filter} to apply
     * @return a filter applied {@link Querying}
     */
    Querying<E, R, U> filter(Filter<E, R> filter);


    /**
     * Apply the given {@link Sort} to the current {@link Querying}.
     * @param sort the {@link Sort} to apply
     * @return a sort applied {@link Querying}
     */
    Querying<E, R, U> sorted(Sort<E, R> sort);


    /**
     * Apply the given sort to the current {@link Querying}.
     * @param sort1 the {@link Sort} to apply
     * @param sort2 the {@link Sort} to apply
     * @return a sort applied {@link Querying}
     */
    Querying<E, R, U> sorted(Sort<E, R> sort1, Sort<E, R> sort2);


    /**
     * Apply the given sort to the current {@link Querying}.
     * @param sort1 the {@link Sort} to apply
     * @param sort2 the {@link Sort} to apply
     * @param sort3 the {@link Sort} to apply
     * @return a sort applied {@link Querying}
     */
    Querying<E, R, U> sorted(Sort<E, R> sort1, Sort<E, R> sort2, Sort<E, R> sort3);


    /**
     * Apply the given sort to the current {@link Querying}.
     * @param sort1 the {@link Sort} to apply
     * @param sort2 the {@link Sort} to apply
     * @param sort3 the {@link Sort} to apply
     * @param sort4 the {@link Sort} to apply
     * @return a sort applied {@link Querying}
     */
    Querying<E, R, U> sorted(Sort<E, R> sort1, Sort<E, R> sort2, Sort<E, R> sort3, Sort<E, R> sort4);


    /**
     * Apply the consisting of the distinct elements to the current {@link Querying}.
     * @return the new {@link Querying} applied distinct
     */
    Querying<E, R, U> distinct();


    /**
     * Apply the consisting of the un distinct elements to the current {@link Querying}.
     * @return the new {@link Querying} applied un distinct
     */
    Querying<E, R, U> unDistinct();


    /**
     * Apply the given {@link Mapper} to the current {@link Querying}.
     * @param mapper the {@link Mapper} to apply
     * @param <Y> type of query result
     * @return a {@link Mapper} applied {@link Querying}
     */
    <Y> Querying<E, R, Y> map(Mapper<E, R, Y> mapper);


    /**
     * Create a {@link Querying} for given root source.
     * @param rootSource the root source
     * @param <E> the type of entity
     * @param <R> the type of root
     * @return a {@link Querying}
     */
    static <E, R extends RootAware<E>> Querying<E, R, E> of(RootSource<E, R> rootSource) {
        return Querying.of(rootSource, Mapper.of(), Filter.empty(), Sorts.empty());
    }


    private static <E, R extends RootAware<E>, U> Querying<E, R, U> of(
            RootSource<E, R> rootSource,
            Mapper<E, R, U> mapper,
            Filter<E, R> filter,
            Sorts<E, R> sorts) {
        return new Querying<>() {
            @Override
            public Querying<E, R, U> filter(Filter<E, R> f) { return Querying.of(rootSource(), mapper(), filter().and(f), sorts()); }
            @Override
            public Querying<E, R, U> sorted(Sort<E, R> sort) { return Querying.of(rootSource(), mapper(), filter(), sorts().and(sort)); }
            @Override
            public Querying<E, R, U> sorted(Sort<E, R> sort1, Sort<E, R> sort2) { return Querying.of(rootSource(), mapper(), filter(), sorts().and(sort1).and(sort2)); }
            @Override
            public Querying<E, R, U> sorted(Sort<E, R> sort1, Sort<E, R> sort2, Sort<E, R> sort3) { return Querying.of(rootSource(), mapper(), filter(), sorts().and(sort1).and(sort2).and(sort3)); }
            @Override
            public Querying<E, R, U> sorted(Sort<E, R> sort1, Sort<E, R> sort2, Sort<E, R> sort3, Sort<E, R> sort4) { return Querying.of(rootSource(), mapper(), filter(), sorts().and(sort1).and(sort2).and(sort3).and(sort4)); }
            @Override
            public Querying<E, R, U> distinct() { return Querying.of(rootSource(), mapper().distinct(true), filter(), sorts()); }
            @Override
            public Querying<E, R, U> unDistinct() { return Querying.of(rootSource(), mapper().distinct(false), filter(), sorts()); }
            @Override
            public <Y> Querying<E, R, Y> map(Mapper<E, R, Y> mapper) { return Querying.of(rootSource(), mapper, filter(), sorts()); }
            @Override
            public RootSource<E, R> rootSource() { return rootSource; }
            @Override
            public Filter<E, R> filter() { return filter; }
            @Override
            public Sorts<E, R> sorts() { return sorts; }
            @Override
            public Mapper<E, R, U> mapper() { return mapper; }
        };
    }

}
