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

import jakarta.persistence.TypedQuery;
import java.util.ArrayList;
import java.util.List;

/**
 * The query hints.
 * @author Naotsugu Kobayashi
 */
public interface Hints {

    /**
     * Apply hint to the given query.
     * @param query the query
     */
    void apply(TypedQuery<?> query);


    /**
     * Add hint.
     * @param hint the {@link Hint}
     * @return the {@link Hints}
     */
    Hints add(Hint hint);


    /**
     * Create the {@link Hints}
     * @param name the hint name
     * @param value the hint value
     * @return the {@link Hints}
     */
    static Hints of(String name, Object value) {
        return Hints.of().add(Hint.of(name, value));
    }


    /**
     * Create the empty hints.
     * @return the {@link Hints}
     */
    static Hints empty() {
        return Hints.of();
    }


    /**
     * Create the {@link Hints}
     * @return the {@link Hints}
     */
    private static Hints of() {
        return new Hints() {
            private final List<Hint> list = new ArrayList<>();
            @Override
            public void apply(TypedQuery<?> query) {
                list.forEach(hint -> hint.apply(query));
            }
            @Override
            public Hints add(Hint hint) {
                list.add(hint);
                return this;
            }
        };
    }

}
