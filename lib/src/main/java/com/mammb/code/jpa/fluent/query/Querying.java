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


public interface Querying<E, R extends RootAware<E>> extends CreateQuery<E, R> {

    Querying<E, R> filter(Filter<E, R> filter);

    Querying<E, R> sorted(Sort<E, R> sort);


    static <E, R extends RootAware<E>> Querying<E, R> of(RootSource<E, R> rootSource) {
        return Querying.of(rootSource, Filter.empty(), Sorts.empty());
    }


    private static <E, R extends RootAware<E>> Querying<E, R> of(
            RootSource<E, R> rootSource, Filter<E, R> filter, Sorts<E, R> sorts) {

        return new Querying<>() {
            @Override
            public Querying<E, R> filter(Filter<E, R> f) { return Querying.of(rootSource, filter.and(f), sorts); }
            @Override
            public Querying<E, R> sorted(Sort<E, R> sort) { return Querying.of(rootSource, filter, sorts.and(sort)); }
            @Override
            public RootSource<E, R> rootSource() { return rootSource; }
            @Override
            public Filter<E, R> filter() { return filter; }
            @Override
            public Sorts<E, R> sorts() { return sorts; }
        };

    }

}
