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
package com.mammb.code.jpa.fluent.repository;

import com.mammb.code.jpa.core.RootAware;
import com.mammb.code.jpa.fluent.query.Filter;
import com.mammb.code.jpa.fluent.query.Sort;
import com.mammb.code.jpa.fluent.query.Sorts;
import com.mammb.code.jpa.fluent.repository.trait.FindAllTrait;
import com.mammb.code.jpa.fluent.repository.trait.FindPageTrait;
import com.mammb.code.jpa.fluent.repository.trait.FindSliceTrait;
import com.mammb.code.jpa.fluent.repository.trait.GetTrait;
import java.io.Serializable;

/**
 * Query base repository.
 * @param <PK> the type of primary key
 * @param <E> the type of entity
 * @param <R> the type of RootEntity
 * @author Naotsugu Kobayashi
 */
public interface QueryRepository<PK extends Serializable, E, R extends RootAware<E>>
    extends GetTrait<PK, E, R>,
            FindAllTrait<E, R>,
            FindSliceTrait<E, R>,
            FindPageTrait<E, R> {

    /**
     * Create a root entity specified filter.
     * @param filter {@link Filter}
     * @return a root entity specified filter
     */
    default Filter<E, R> filter(Filter<E, R> filter) { return Filter.of(filter); }


    /**
     * Create a root entity specified sort.
     * @param sort {@link Sorts}
     * @return a root entity specified sorts
     */
    default Sorts<E, R> sort(Sort<E, R> sort) { return Sorts.of(sort); }

}
