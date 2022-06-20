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
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

import java.io.Serializable;
import java.util.Objects;

/**
 * Criteria specification.
 * @param <E> the type of entity
 * @author Naotsugu Kobayashi
 */
public interface CriteriaSpec<E> extends Serializable {

    Predicate toPredicate(Root<E> root, CriteriaQuery<?> query, CriteriaBuilder builder);


    default CriteriaSpec<E> and(CriteriaSpec<E> other) {
        return SpecComposition.composed(this, other, CriteriaBuilder::and);
    }


    default CriteriaSpec<E> or(CriteriaSpec<E> other) {
        return SpecComposition.composed(this, other, CriteriaBuilder::or);
    }


    static <E> CriteriaSpec<E> not(CriteriaSpec<E> spec) {
        return Objects.isNull(spec)
                ? (root, query, builder) -> null
                : (root, query, builder) -> builder.not(spec.toPredicate(root, query, builder));
    }

    class SpecComposition {

        interface Combiner extends Serializable {
            Predicate combine(CriteriaBuilder builder, Predicate lhs, Predicate rhs);
        }

        static <E> CriteriaSpec<E> composed(CriteriaSpec<E> lhs, CriteriaSpec<E> rhs, Combiner combiner) {

            return (root, query, builder) -> {

                Predicate that  = toPredicate(lhs, root, query, builder);
                Predicate other = toPredicate(rhs, root, query, builder);

                if (Objects.isNull(that)) {
                    return other;
                }

                return Objects.isNull(other)
                        ? that
                        : combiner.combine(builder, that, other);
            };
        }

        private static <E> Predicate toPredicate(
            CriteriaSpec<E> specification, Root<E> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
            return Objects.isNull(specification) ? null : specification.toPredicate(root, query, builder);
        }
    }

}
