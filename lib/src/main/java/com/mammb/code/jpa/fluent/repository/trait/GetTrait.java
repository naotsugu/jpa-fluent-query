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

import java.io.Serializable;
import java.util.Optional;

public interface GetTrait<PK extends Serializable, E, R extends RootAware<E>> extends EntityManagerAware, RootSourceAware<E, R> {

    default Optional<E> getReference(PK id) {
        return Optional.ofNullable(em().getReference(rootSource().rootClass(), id));
    }

    default Optional<E> get(PK id) {
        return Optional.ofNullable(em().find(rootSource().rootClass(), id));
    }

    default E get(E entity) {
        return em().find(rootSource().rootClass(),
            em().getEntityManagerFactory().getPersistenceUnitUtil().getIdentifier(entity));
    }

}
