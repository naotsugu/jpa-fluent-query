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

import com.mammb.code.jpa.core.Mapper;
import com.mammb.code.jpa.core.RootAware;
import com.mammb.code.jpa.core.RootSource;

import java.util.List;
import java.util.Optional;

/**
 * Create Query helper interface.
 * @param <E> the entity of root
 * @param <R> the root element
 * @author Naotsugu Kobayashi
 */
public interface CreateQuery<E, R extends RootAware<E>, U> {

    RootSource<E, R> rootSource();

    Filter<E, R> filter();

    Sorts<E, R> sorts();

    Mapper<E, R, U> mapper();

    default Query<Long> count() {
        return em -> QueryHelper.countQuery(em, rootSource(), filter()).getSingleResult();
    }

    default Query<Optional<U>> toSingle() {
        return em -> Optional.ofNullable(QueryHelper.query(em, rootSource(), mapper(), filter(), sorts()).getSingleResult());
    }

    default Query<List<U>> toList() {
        return em -> QueryHelper.query(em, rootSource(), mapper(), filter(), sorts()).getResultList();
    }

    default Query<Slice<U>> toSlice(SlicePoint slicePoint) {
        return em -> QueryHelper.slice(em, rootSource(), mapper(), filter(), sorts(), slicePoint);
    }

    default Query<Page<U>> toPage(SlicePoint slicePoint) {
        return em -> QueryHelper.page(em, rootSource(), mapper(), filter(), sorts(), slicePoint);
    }

}
