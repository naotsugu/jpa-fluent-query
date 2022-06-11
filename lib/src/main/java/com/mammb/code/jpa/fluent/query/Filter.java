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
