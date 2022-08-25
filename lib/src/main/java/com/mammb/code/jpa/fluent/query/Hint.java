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

import jakarta.persistence.EntityGraph;
import jakarta.persistence.TypedQuery;

/**
 * The query hint.
 * @author Naotsugu Kobayashi
 */
public interface Hint {

    /**
     * Get the name of hint.
     * @return the name of hint
     */
    String getName();


    /**
     * Get the value of hint.
     * @return the value of hint
     */
    Object getValue();


    /**
     * Apply hint to the given query.
     * @param query the query
     */
    void apply(TypedQuery<?> query);


    /**
     * Create a loadgraph hint.
     * @param entityGraph the {@link EntityGraph}
     * @return the hint element
     */
    static Hint loadOf(EntityGraph<?> entityGraph) {
        return Hint.of("jakarta.persistence.loadgraph", entityGraph);
    }


    /**
     * Create a fetchgraph hint.
     * @param entityGraph the {@link EntityGraph}
     * @return the hint element
     */
    static Hint fetchOf(EntityGraph<?> entityGraph) {
        return Hint.of("jakarta.persistence.fetchgraph", entityGraph);
    }


    /**
     * Create the hint element.
     * @param name the name of hint
     * @param value the value of hint
     * @return the hint element
     */
    static Hint of(String name, Object value) {
        return new Hint() {
            @Override
            public String getName() { return name; }
            @Override
            public Object getValue() { return value; }
            @Override
            public void apply(TypedQuery<?> query) { query.setHint(getName(), getValue()); }
        };
    }

}
