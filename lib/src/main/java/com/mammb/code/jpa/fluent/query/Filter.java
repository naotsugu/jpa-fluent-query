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
import jakarta.persistence.criteria.Predicate;
import java.io.Serializable;
import java.util.Objects;

public interface Filter<E, R extends RootAware<E>> {

    Predicate apply(R root);

    default Filter<E, R> and(Filter<E, R> other) {
        return Composition.composed(this, other, CriteriaBuilder::and);
    }

    default Filter<E, R> or(Filter<E, R> other) {
        return Composition.composed(this, other, CriteriaBuilder::or);
    }


    static <E, R extends RootAware<E>> Filter<E, R> empty() {
        return root -> null;
    }

    class Composition {

        interface Combiner extends Serializable {
            Predicate combine(CriteriaBuilder builder, Predicate lhs, Predicate rhs);
        }

        static <E, R extends RootAware<E>> Filter<E, R> composed(
            Filter<E, R> lhs, Filter<E, R> rhs, Combiner combiner) {

            return root -> {

                Predicate that  = Objects.isNull(lhs) ? null : lhs.apply(root);
                Predicate other = Objects.isNull(rhs) ? null : rhs.apply(root);

                if (Objects.isNull(that)) {
                    return other;
                }

                return Objects.isNull(other)
                    ? that
                    : combiner.combine(root.builder(), that, other);
            };
        }
    }

}
