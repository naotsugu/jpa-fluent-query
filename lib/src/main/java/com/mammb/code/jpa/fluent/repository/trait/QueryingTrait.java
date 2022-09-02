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

import com.mammb.code.jpa.fluent.core.RootAware;
import com.mammb.code.jpa.fluent.core.RootSourceAware;
import com.mammb.code.jpa.fluent.query.Querying;

/**
 * QueryingTrait.
 * @param <E> the type of entity
 * @param <R> the type of root
 * @author Naotsugu Kobayashi
 */
public interface QueryingTrait<E, R extends RootAware<E>> extends RootSourceAware<E, R> {

    /**
     * Get the Querying.
     * @return the {@link Querying}
     */
    default Querying<E, R, E> querying() {
        return Querying.of(rootSource());
    }

}
