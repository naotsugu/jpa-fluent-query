package com.mammb.code.jpa.fluent.query;

import com.mammb.code.jpa.core.RootAware;
import com.mammb.code.jpa.core.SubRootAware;
import com.mammb.code.jpa.core.SubRootSource;
import jakarta.persistence.criteria.Predicate;

public interface SubQuery<E, R extends SubRootAware<E>, U> {

    SubRootSource<E, R, U> rootSource();

    Filter<E, R> filter();

    SubQuery<E, R, U> filter(Filter<E, R> filter);

    default Predicate exists(RootAware<?> rootAware) {
        return rootAware.builder().exists(
            QueryHelper.subQuery(rootAware.query(), rootAware.builder(), rootSource(), filter()));
    }

    static <E, R extends SubRootAware<E>, U> SubQuery<E, R, U> of(SubRootSource<E, R, U> subRootSource) {
        return SubQuery.of(subRootSource, Filter.empty());
    }

    private static <E, R extends SubRootAware<E>, U> SubQuery<E, R, U> of(
            SubRootSource<E, R, U> subRootSource, Filter<E, R> filter) {
        return new SubQuery<>() {
            @Override
            public SubQuery<E, R, U> filter(Filter<E, R> f) { return SubQuery.of(rootSource(), filter().and(f)); }
            @Override
            public SubRootSource<E, R, U> rootSource() { return subRootSource; }
            @Override
            public Filter<E, R> filter() { return filter; }
        };
   }
}
