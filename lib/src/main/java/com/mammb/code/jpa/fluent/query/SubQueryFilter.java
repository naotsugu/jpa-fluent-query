package com.mammb.code.jpa.fluent.query;

import com.mammb.code.jpa.core.RootAware;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;

import java.io.Serializable;
import java.util.Objects;

public interface SubQueryFilter<E, R extends RootAware<E>> {

    Predicate apply(R root, Subquery<?> subquery);


    static <E, R extends RootAware<E>> SubQueryFilter<E, R> of(Filter<E, R> filter) {
        return (root, subquery) -> filter.apply(root);
    }


    static <E1, R1 extends RootAware<E1>, E, R extends RootAware<E>> SubQueryFilter<E, R> correlateOf(
            Root<E1> root, R1 correlateRoot, BiFilter<E1, R1, E, R> biFilter) {
        return (R rootAware2, Subquery<?> subquery) -> {
                Root<E1> correlate = subquery.correlate(root);
                @SuppressWarnings("unchecked")
                R1 rootAware1 = (R1) correlateRoot.with(correlate, subquery);
                return biFilter.apply(rootAware1, rootAware2);
        };
    }


    static <E, R extends RootAware<E>> SubQueryFilter<E, R> empty() {
        return (root, subquery) -> null;
    }


    /**
     * ANDs the given {@link SubQueryFilter} to the current one.
     * @param other a {@link SubQueryFilter} for AND target
     * @return a {@link SubQueryFilter}
     */
    default SubQueryFilter<E, R> and(SubQueryFilter<E, R> other) {
        return SubQueryFilter.Composition.composed(this, other, CriteriaBuilder::and);
    }


    /**
     * ORs the given {@link SubQueryFilter} to the current one.
     * @param other a {@link SubQueryFilter} for OR target
     * @return a {@link SubQueryFilter}
     */
    default SubQueryFilter<E, R> or(SubQueryFilter<E, R> other) {
        return SubQueryFilter.Composition.composed(this, other, CriteriaBuilder::or);
    }


    /**
     * The {@link Filter} composition helper.
     */
    class Composition {

        interface Combiner extends Serializable {
            Predicate combine(CriteriaBuilder builder, Predicate lhs, Predicate rhs);
        }

        static <E, R extends RootAware<E>> SubQueryFilter<E, R> composed(
            SubQueryFilter<E, R> lhs, SubQueryFilter<E, R> rhs, SubQueryFilter.Composition.Combiner combiner) {

            return (root, subquery) -> {

                Predicate that  = Objects.isNull(lhs) ? null : lhs.apply(root, subquery);
                Predicate other = Objects.isNull(rhs) ? null : rhs.apply(root, subquery);

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

