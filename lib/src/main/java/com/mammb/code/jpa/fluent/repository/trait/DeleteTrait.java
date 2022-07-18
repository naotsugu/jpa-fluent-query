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

/**
 * Delete trait.
 * @param <E> the type of entity
 * @author Naotsugu Kobayashi
 */
public interface DeleteTrait<E> extends EntityManagerAware {

    /**
     * Delete the given entity.
     * @param entity to be deleted
     */
    default void delete(E entity) {
        em().remove(entity);
    }

    /**
     * Delete the given entity and flash.
     * @param entity to be deleted
     */
    default void deleteAndFlash(E entity) {
        delete(entity);
        em().flush();
    }

}
