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

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Order;
import jakarta.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Sort specification.
 * @param <T> the type of entity
 */
public interface SortSpec<T> {

    List<Order> toOrders(Root<T> root, CriteriaBuilder builder);

    default SortSpec<T> and(SortSpec<T> that) {

        return (root, builder) -> {

            var lhs = this.toOrders(root, builder);
            var rhs = that.toOrders(root, builder);

            List<Order> orders = new ArrayList<>();
            if (Objects.nonNull(lhs)) {
                orders.addAll(lhs);
            }
            if (Objects.nonNull(rhs)) {
                orders.addAll(rhs);
            }

            return orders;

        };
    }

}
