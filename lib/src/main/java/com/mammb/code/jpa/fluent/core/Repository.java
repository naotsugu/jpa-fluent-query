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
package com.mammb.code.jpa.fluent.core;

import java.io.Serializable;

/**
 * Repository interface.
 *
 * @param <PK> the type of the id of the entity the repository manages
 * @param <E> the entity type the repository manages
 * @param <R> the entity type as root aware
 * @author Naotsugu Kobayashi
 */
public interface Repository<PK extends Serializable, E, R extends RootAware<E>> {

    /**
     * Get the {@link RootSource}.
     * @return the {@link RootSource}
     */
    RootSource<E, R> rootSource();

}
