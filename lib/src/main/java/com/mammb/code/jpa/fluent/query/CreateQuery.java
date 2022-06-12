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

import java.util.List;

/**
 * Create Query helper interface.
 * @param <E> the entity of root
 * @param <R> the root element
 * @author Naotsugu Kobayashi
 */
public interface CreateQuery<E, R extends RootAware<E>> {

    RootSource<E, R> rootSource();

    Filter<E, R> filter();

    Sorts<E, R> sorts();


    default Query<Long> count() {
        return em -> QueryHelper.countQuery(em, rootSource(), filter()).getSingleResult();
    }

    default Query<List<E>> toList() {
        return em -> QueryHelper.query(em, rootSource(), filter(), sorts()).getResultList();
    }

    default Query<Slice<E>> toSlice(SlicePoint slicePoint) {
        return em -> QueryHelper.slice(em, rootSource(), filter(), sorts(), slicePoint);
    }

    default Query<Page<E>> toPage(SlicePoint slicePoint) {
        return em -> QueryHelper.page(em, rootSource(), filter(), sorts(), slicePoint);
    }

}
