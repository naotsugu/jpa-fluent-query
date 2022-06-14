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

import com.mammb.code.jpa.core.EntityManagerAware;
import com.mammb.code.jpa.core.RootAware;
import com.mammb.code.jpa.core.RootSourceAware;
import com.mammb.code.jpa.fluent.query.Filter;
import com.mammb.code.jpa.fluent.query.QueryHelper;
import com.mammb.code.jpa.fluent.query.Slice;
import com.mammb.code.jpa.fluent.query.SlicePoint;
import com.mammb.code.jpa.fluent.query.Sorts;


public interface FindSliceTrait<E, R extends RootAware<E>> extends EntityManagerAware, RootSourceAware<E, R> {

    default Slice<E> findSlice(Filter<E, R> filter, Sorts<E, R> sorts, SlicePoint slicePoint) {
        return QueryHelper.slice(em(), rootSource(), filter, sorts, slicePoint);
    }

}
