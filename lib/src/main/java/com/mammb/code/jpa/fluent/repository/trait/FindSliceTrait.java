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
package com.mammb.code.jpa.fluent.repository.trait;

import com.mammb.code.jpa.fluent.core.EntityManagerAware;
import com.mammb.code.jpa.fluent.query.Hints;
import com.mammb.code.jpa.fluent.query.Mapper;
import com.mammb.code.jpa.fluent.core.RootAware;
import com.mammb.code.jpa.fluent.core.RootSourceAware;
import com.mammb.code.jpa.fluent.query.Filter;
import com.mammb.code.jpa.fluent.query.QueryBuilder;
import com.mammb.code.jpa.fluent.query.Slice;
import com.mammb.code.jpa.fluent.query.SlicePoint;
import com.mammb.code.jpa.fluent.query.SliceRequest;
import com.mammb.code.jpa.fluent.query.Sorts;

/**
 * FindSliceTrait.
 * @param <E> the type of entity
 * @param <R> the type of root
 * @author Naotsugu Kobayashi
 */
public interface FindSliceTrait<E, R extends RootAware<E>> extends EntityManagerAware, RootSourceAware<E, R> {

    /**
     * Find slice.
     * @param request the slice request
     * @return the {@link Slice}
     */
    default Slice<E> findSliceBy(SliceRequest<E, R> request) {
        return findSlice(request.getSlicePoint(), request.getFilter(), request.getSorts());
    }


    /**
     * Find slice.
     * @param slicePoint a {@link SlicePoint}
     * @param filter a {@link Filter}
     * @return the {@link Slice}
     */
    default Slice<E> findSlice(SlicePoint slicePoint, Filter<E, R> filter) {
        return findSlice(slicePoint, filter, Sorts.empty());
    }


    /**
     * Find slice.
     * @param slicePoint a {@link SlicePoint}
     * @param filter a {@link Filter}
     * @param sorts a {@link Sorts}
     * @return the {@link Slice}
     */
    default Slice<E> findSlice(SlicePoint slicePoint, Filter<E, R> filter, Sorts<E, R> sorts) {
        return QueryBuilder.slice(em(), rootSource(), Mapper.of(), filter, sorts, slicePoint, Hints.empty());
    }

}
