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
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Order;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * Represents a sort specification.
 * @param <E> the type of entity
 * @param <R> the type of root
 * @author Naotsugu Kobayashi
 */
@FunctionalInterface
public interface Sorts<E, R extends RootAware<E>> {

    /**
     * Create the {@link Order} condition.
     * @param root the root fo sort target
     * @return an {@link Order} list
     */
    List<Order> apply(R root);


    /**
     * AND composite the specified sort condition to the current one.
     * @param other a {@link Sort} for AND target
     * @return the {@link Sorts} after composite
     */
    default Sorts<E, R> and(Sort<E, R> other) {
        return Sorts.add(this, other);
    }


    /**
     * Create an empty {@link Sorts}.
     * @param <E> the type of entity
     * @param <R> the type of root
     * @return an empty {@link Sorts}
     */
    static <E, R extends RootAware<E>> Sorts<E, R> empty() {
        return root -> List.of();
    }


    /**
     * Create a {@link Sorts} from the given {@link Sort}.
     * @param sort the source of sort
     * @param <E> the type of entity
     * @param <R> the type of root
     * @return a {@link Sorts}
     */
    static <E, R extends RootAware<E>> Sorts<E, R> of(Sort<E, R> sort) {
        return root -> List.of(sort.apply(root));
    }


    /**
     * Create a {@link Sorts} from the given {@link Sort}.
     * @param lhs the left hands of sort
     * @param rhs the right hands of sort
     * @param <E> the type of entity
     * @param <R> the type of root
     * @return a {@link Sorts}
     */
    static <E, R extends RootAware<E>> Sorts<E, R> of(Sort<E, R> lhs, Sort<E, R> rhs) {
        return root -> List.of(lhs.apply(root), rhs.apply(root));
    }


    /**
     * Create a {@link Sorts} from the given {@link Sort}s.
     * @param sort the source of sort
     * @param sorts the remaining sort
     * @param <E> the type of entity
     * @param <R> the type of root
     * @return a {@link Sorts}
     */
    @SafeVarargs
    static <E, R extends RootAware<E>> Sorts<E, R> of(Sort<E, R> sort, Sort<E, R>... sorts) {
        return root -> Arrays.stream(sorts).reduce(Sorts.of(sort), Sorts::and, Sorts::plus).apply(root);
    }


    private static <E, R extends RootAware<E>> Sorts<E, R> add(Sorts<E, R> lhs, Sort<E, R> rhs) {
        return root -> {
            List<Order> orders = new ArrayList<>(lhs.apply(root));
            orders.add(rhs.apply(root));
            orders.removeIf(Objects::isNull);
            return orders;
        };
    }


    private static <E, R extends RootAware<E>> Sorts<E, R> plus(Sorts<E, R> lhs, Sorts<E, R> rhs) {
        return root -> {
            List<Order> orders = new ArrayList<>(lhs.apply(root));
            List<Order> rh = new ArrayList<>(rhs.apply(root));
            orders.addAll(rh);
            orders.removeIf(Objects::isNull);
            return orders;
        };
    }

}
