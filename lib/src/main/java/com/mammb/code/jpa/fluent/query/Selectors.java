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

import com.mammb.code.jpa.core.RootAware;
import jakarta.persistence.criteria.Selection;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * The selector for multi SELECT clause.
 *
 * @param <E> the type of entity
 * @param <R> the type of root
 * @author Naotsugu Kobayashi
 */
@FunctionalInterface
public interface Selectors<E, R extends RootAware<E>> {

    List<Selection<?>> apply(R root);

    default Selectors<E, R> and(Selector<E, R, ?> other) {
        return Selectors.add(this, other);
    }

    static <E, R extends RootAware<E>> Selectors<E, R> empty() {
        return root -> List.of();
    }


    static <E, R extends RootAware<E>> Selectors<E, R> of(Selector<E, R, ?> selector) {
        return root -> List.of(selector.apply(root));
    }

    static <E, R extends RootAware<E>> Selectors<E, R> of(Selector<E, R, ?> lhs, Selector<E, R, ?> rhs) {
        return root -> List.of(lhs.apply(root), rhs.apply(root));
    }

    private static <E, R extends RootAware<E>> Selectors<E, R> add(Selectors<E, R> lhs, Selector<E, R, ?> rhs) {
        return root -> {
            List<Selection<?>> orders = new ArrayList<>(lhs.apply(root));
            orders.add(rhs.apply(root));
            orders.removeIf(Objects::isNull);
            return orders;
        };
    }

}
