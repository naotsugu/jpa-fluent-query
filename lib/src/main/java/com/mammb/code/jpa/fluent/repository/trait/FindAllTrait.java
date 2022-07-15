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
import com.mammb.code.jpa.fluent.query.Mapper;
import com.mammb.code.jpa.core.RootAware;
import com.mammb.code.jpa.core.RootSourceAware;
import com.mammb.code.jpa.fluent.query.Filter;
import com.mammb.code.jpa.fluent.query.QueryBuilder;
import com.mammb.code.jpa.fluent.query.Request;
import com.mammb.code.jpa.fluent.query.Sorts;
import java.util.List;

/**
 * FindAllTrait.
 * @param <E> the type of entity
 * @param <R> the type of root
 * @author Naotsugu Kobayashi
 */
public interface FindAllTrait<E, R extends RootAware<E>> extends EntityManagerAware, RootSourceAware<E, R> {

    /**
     * Get the list of target entity.
     * @return the list of target entity
     */
    default List<E> findAll() {
        return findAll(Filter.empty(), Sorts.empty());
    }


    /**
     * Get the list of target entity by the given request.
     * @param request the {@link Request}
     * @return the list of target entity
     */
    default List<E> findAll(Request<E, R> request) {
        return findAll(request.getFilter(), request.getSorts());
    }


    /**
     * Get the list of target entity by the given filter.
     * @param filter the list of target entity
     * @return the list of target entity
     */
    default List<E> findAll(Filter<E, R> filter) {
        return findAll(filter, Sorts.empty());
    }


    /**
     * Get the list of target entity by the given filter and sort conditions.
     * @param filter the list of target entity
     * @param sorts the sort conditions
     * @return the list of target entity
     */
    default List<E> findAll(Filter<E, R> filter, Sorts<E, R> sorts) {
        return QueryBuilder.query(em(), rootSource(), Mapper.of(), filter, sorts).getResultList();
    }


    /**
     * Get the count of target entity.
     * @return the count of target entity
     */
    default long count() {
        return count(Filter.empty());
    }


    /**
     * Get the count of target entity by the given filter conditions.
     * @param filter the list of target entity
     * @return the count of target entity
     */
    default long count(Filter<E, R> filter) {
        return QueryBuilder.countQuery(em(), rootSource(), filter).getSingleResult();
    }

}
