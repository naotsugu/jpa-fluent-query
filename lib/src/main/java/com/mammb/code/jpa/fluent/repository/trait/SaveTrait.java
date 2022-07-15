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
import java.util.Objects;

/**
 * SaveTrait.
 * @param <E> the type of entity
 * @author Naotsugu Kobayashi
 */
public interface SaveTrait<E> extends EntityManagerAware {

    /**
     * Save the given entity.
     * @param entity the entity to be saved
     * @return the saved entity
     */
    default E save(E entity) {
        if (Objects.isNull(em().getEntityManagerFactory()
            .getPersistenceUnitUtil().getIdentifier(entity))) {
            em().persist(entity);
        } else {
            em().merge(entity);
        }
        return entity;
    }

    /**
     * Save the given entity and flash.
     * @param entity the entity to be saved
     * @return the saved entity
     */
    default E saveAndFlash(E entity) {
        entity = save(entity);
        em().flush();
        return entity;
    }

}
