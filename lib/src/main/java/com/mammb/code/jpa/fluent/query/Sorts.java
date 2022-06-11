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
import jakarta.persistence.criteria.Order;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public interface Sorts<E, R extends RootAware<E>> {

    List<Order> apply(R root);

    default Sorts<E, R> and(Sort<E, R> other) {
        return Sorts.of(this, other);
    }


    static <E, R extends RootAware<E>> Sorts<E, R> empty() {
        return root -> List.of();
    }

    static <E, R extends RootAware<E>> Sorts<E, R> of(Sort<E, R> lhs, Sort<E, R> rhs) {
        return root -> List.of(lhs.apply(root), rhs.apply(root));
    }

    static <E, R extends RootAware<E>> Sorts<E, R> of(Sorts<E, R> lhs, Sort<E, R> rhs) {
        return root -> {
            List<Order> orders = new ArrayList<>(lhs.apply(root));
            orders.add(rhs.apply(root));
            orders.removeIf(Objects::isNull);
            return orders;
        };
    }

}
