package com.mammb.code.jpa.fluent.query;

import com.mammb.code.jpa.core.RootAware;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;

public interface CorrelateFilter<E1, R1 extends RootAware<E1>, E2, R2 extends RootAware<E2>> {

    Filter<E2, R2> apply(Root<?> root1, Subquery<?> subquery);


    @SuppressWarnings("unchecked")
    static <E1, R1 extends RootAware<E1>, E2, R2 extends RootAware<E2>> CorrelateFilter<E1, R1, E2, R2> of(
            R1 correlateRoot, BiFilter<E1, R1, E2, R2> biFilter) {
        return (Root<?> root1, Subquery<?> subquery) -> {
                Root<E1> correlate = (Root<E1>) subquery.correlate(root1);
                R1 rootAware1 = (R1) correlateRoot.with(correlate, subquery);
                return (R2 rootAware2) -> biFilter.apply(rootAware1, rootAware2);
        };
    }


    static <E1, R1 extends RootAware<E1>, E2, R2 extends RootAware<E2>> CorrelateFilter<E1, R1, E2, R2> empty() {
        return (r, sq) -> Filter.empty();
    }

}

