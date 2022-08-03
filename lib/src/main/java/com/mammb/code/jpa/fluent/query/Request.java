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

import java.util.Objects;

/**
 * The query request.
 * @param <E> the type of entity
 * @param <R> the type of root
 * @author Naotsugu Kobayashi
 */
public interface Request<E, R extends RootAware<E>> {

    /**
     * Get the {@link Filter}.
     * @return the {@link Filter}
     */
    Filter<E, R> getFilter();


    /**
     * Get the {@link Sorts}.
     * @return the {@link Sorts}
     */
    default Sorts<E, R> getSorts() {
        return Sorts.empty();
    }


    /**
     * Create a {@link Request} with the given filter.
     * @param filter the {@link Filter}
     * @param <E> the type of entity
     * @param <R> the type of root
     * @return a {@link Request}
     */
    static <E, R extends RootAware<E>> Request<E, R> of(Filter<E, R> filter) {
        return of(filter, Sorts.empty());
    }


    /**
     * Create the {@link SlicePoint} with given sorts.
     * @param sorts a {@link Sorts}
     * @return {@link SlicePoint}
     */
    default Request<E, R> withSorts(Sorts<E, R> sorts) {
        return of(getFilter(), sorts);
    }


    /**
     * Create a {@link Request}
     * @param filter a {@link Filter}
     * @param sorts a {@link Sorts}
     * @param <E> the type of entity
     * @param <R> the type of root
     * @return a {@link Request}
     */
    static <E, R extends RootAware<E>> Request<E, R> of(Filter<E, R> filter, Sorts<E, R> sorts) {
        return new Request<>() {
            @Override
            public Filter<E, R> getFilter() { return Objects.isNull(filter) ? Filter.empty() : filter; }
            @Override
            public Sorts<E, R> getSorts() {
                return Objects.isNull(sorts) ? Sorts.empty() : sorts;
            }
        };
    }

}
