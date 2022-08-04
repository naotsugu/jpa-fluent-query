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
import com.mammb.code.jpa.fluent.query.Page;
import com.mammb.code.jpa.fluent.query.QueryBuilder;
import com.mammb.code.jpa.fluent.query.Request;
import com.mammb.code.jpa.fluent.query.SlicePoint;
import com.mammb.code.jpa.fluent.query.SliceRequest;
import com.mammb.code.jpa.fluent.query.Sort;
import com.mammb.code.jpa.fluent.query.Sorts;

/**
 * FindPageTrait.
 * @param <E> the type of entity
 * @param <R> the type of root
 * @author Naotsugu Kobayashi
 */
public interface FindPageTrait<E, R extends RootAware<E>> extends EntityManagerAware, RootSourceAware<E, R> {

    /**
     * Find page.
     * @param request the slice request
     * @return the {@link Page}
     */
    default Page<E> findPageBy(SliceRequest<E, R> request) {
        return findPage(request.getSlicePoint(), request.getFilter(), request.getSorts());
    }


    /**
     * Find page.
     * @param slicePoint a {@link SlicePoint}
     * @param filter a {@link Filter}
     * @return the {@link Page}
     */
    default Page<E> findPage(SlicePoint slicePoint, Filter<E, R> filter) {
        return findPage(slicePoint, filter, Sorts.empty());
    }


    /**
     * Find page.
     * @param slicePoint a {@link SlicePoint}
     * @param filter a {@link Filter}
     * @param sorts a {@link Sorts}
     * @return the {@link Page}
     */
    default Page<E> findPage(SlicePoint slicePoint, Filter<E, R> filter, Sorts<E, R> sorts) {
        return QueryBuilder.page(em(), rootSource(), Mapper.of(), filter, sorts, slicePoint, Hints.empty());
    }

}
