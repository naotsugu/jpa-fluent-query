
package com.mammb.code.jpa.fluent.query;

import com.mammb.code.jpa.core.BuilderAware;
import com.mammb.code.jpa.core.QueryAware;
import com.mammb.code.jpa.core.RootAware;
import com.mammb.code.jpa.core.SubRoot;
import com.mammb.code.jpa.core.SubRootSource;
import jakarta.persistence.criteria.AbstractQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;

public interface SubQuery<E, R extends SubRoot<E, U>, U> extends QueryAware<AbstractQuery<?>>, BuilderAware {

    SubRootSource<E, R, U> subRootSource();

    Filter<E, R> filter();

    SubQuery<E, R, U> filter(Filter<E, R> filter);


    default Predicate exists() {
        return builder().exists(QueryHelper.subQuery(query(), builder(), subRootSource(), filter()));
    }

    static <E, R extends SubRoot<E, U>, U> SubQuery<E, R, U> of(
            SubRootSource<E, R, U> subRootSource, RootAware<?> rootAware) {
        return SubQuery.of(subRootSource, rootAware, rootAware, Filter.empty());
    }

    private static <E, R extends SubRoot<E, U>, U> SubQuery<E, R, U> of(
            SubRootSource<E, R, U> subRootSource, QueryAware<?> queryAware, BuilderAware builderAware, Filter<E, R> filter) {
        return new SubQuery<>() {
            @Override
            public SubQuery<E, R, U> filter(Filter<E, R> f) { return SubQuery.of(
                subRootSource(), queryAware, builderAware, filter().and(f)); }
            @Override
            public SubRootSource<E, R, U> subRootSource() { return subRootSource; }
            @Override
            public Filter<E, R> filter() { return filter; }
            @Override
            public CriteriaBuilder builder() { return builderAware.builder(); }
            @Override
            public AbstractQuery<?> query() { return queryAware.query(); }
        };
   }
}
