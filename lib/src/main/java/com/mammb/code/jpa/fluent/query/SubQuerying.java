package com.mammb.code.jpa.fluent.query;

import com.mammb.code.jpa.core.Criteria;
import com.mammb.code.jpa.core.QueryContext;
import com.mammb.code.jpa.core.RootAware;
import com.mammb.code.jpa.core.RootSource;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Predicate;

public interface SubQuerying<E, R extends RootAware<E>, U> {

    SubQuerying<E, R, U> filter(Filter<E, R> filter);

    <E1, R1 extends RootAware<E1>> SubQuerying<E, R, U> filter(R1 correlateRoot, BiFilter<E1, R1, E, R> filter);

    RootSource<E, R> rootSource();
    Mapper<E, R, U> mapper();
    Filter<E, R> filter();
    CorrelateFilter<?, ?, E, R> correlateFilter();

    default <U> Expression<U> to(Class<U> resultType, Criteria.ExpressionSelector<E, R, U> selector) {
        return QueryBuilder.subQuery(rootSource(), filter(),
            Mapper.subQuery(resultType, ExpressionSelector.of(selector)), correlateFilter());
    }

    default Expression<Long> count() {
        return QueryContext.builder().count(QueryBuilder.subQuery(rootSource(), filter(), mapper(), correlateFilter()));
    }

    default Predicate exists() {
        return QueryContext.builder().exists(QueryBuilder.subQuery(rootSource(), filter(), mapper(), correlateFilter()));
    }

    static <E, R extends RootAware<E>> SubQuerying<E, R, E> of(RootSource<E, R> subRootSource) {
        return SubQuerying.of(subRootSource, Mapper.subQuery(), Filter.empty(), CorrelateFilter.empty());
    }

    static <E, R extends RootAware<E>> SubQuerying<E, R, E> of(R correlate) {
        return SubQuerying.of(RootSource.directly(correlate, correlate.type()), Mapper.correlate(), Filter.empty(), CorrelateFilter.empty());
    }

    private static <E, R extends RootAware<E>, U> SubQuerying<E, R, U> of(
            RootSource<E, R> subRootSource,
            Mapper<E, R, U> mapper,
            Filter<E, R> filter,
            CorrelateFilter<?, ?, E, R> correlateFilter) {

        return new SubQuerying<>() {
            @Override
            public SubQuerying<E, R, U> filter(Filter<E, R> f) {
                return SubQuerying.of(rootSource(), mapper(), filter().and(f), correlateFilter());
            }
            @Override
            public <E1, R1 extends RootAware<E1>> SubQuerying<E, R, U> filter(R1 correlateRoot, BiFilter<E1, R1, E, R> biFilter) {
                return SubQuerying.of(rootSource(), mapper(), filter(), CorrelateFilter.of(correlateRoot, biFilter));
            }
            @Override
            public Filter<E, R> filter() { return filter; }
            @Override
            public RootSource<E, R> rootSource() { return subRootSource; }
            @Override
            public Mapper<E, R, U> mapper() { return mapper; }
            @Override
            public CorrelateFilter<?, ?, E, R> correlateFilter() { return correlateFilter; }
        };

    }

}
