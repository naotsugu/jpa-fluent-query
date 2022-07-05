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
package com.mammb.code.jpa.core;

import jakarta.persistence.criteria.CriteriaQuery;

/**
 * Criteria root.
 * @param <E> the type of entity
 * @param <U> type of query result
 * @author Naotsugu Kobayashi
 */
public interface CriteriaRoot<E, U> extends RootAware<E> {
    @Override CriteriaQuery<U> query();
}
