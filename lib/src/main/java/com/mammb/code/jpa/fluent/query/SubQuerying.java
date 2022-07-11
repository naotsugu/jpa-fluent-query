package com.mammb.code.jpa.fluent.query;

import com.mammb.code.jpa.core.Criteria;
import com.mammb.code.jpa.core.QueryContext;
import com.mammb.code.jpa.core.RootAware;
import com.mammb.code.jpa.core.RootSource;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

public interface SubQuerying<E, R extends RootAware<E>, U> {

    SubQuerying<E, R, U> filter(Filter<E, R> filter);

    <E1, R1 extends RootAware<E1>> SubQuerying<E, R, U> filter(R1 correlateRoot, BiFilter<E1, R1, E, R> filter);

    RootSource<E, R> rootSource();
    Mapper<E, R, U> mapper();
    SubQueryFilter<E, R> filter();

    default <U> Expression<U> to(Class<U> resultType, Criteria.ExpressionSelector<E, R, U> selector) {
        return QueryBuilder.subQuery(rootSource(), filter(),
            Mapper.subQuery(resultType, ExpressionSelector.of(selector)));
    }

    default Expression<Long> count() {
        return QueryContext.builder().count(QueryBuilder.subQuery(rootSource(), filter(), mapper()));
    }

    default Predicate exists() {
        return QueryContext.builder().exists(QueryBuilder.subQuery(rootSource(), filter(), mapper()));
    }

    static <E, R extends RootAware<E>> SubQuerying<E, R, E> of(RootSource<E, R> subRootSource) {
        return SubQuerying.of(subRootSource, Mapper.subQuery(), SubQueryFilter.empty());
    }

    static <E, R extends RootAware<E>> SubQuerying<E, R, E> of(R correlate) {
        return SubQuerying.of(RootSource.directly(correlate, correlate.type()), Mapper.correlate(), SubQueryFilter.empty());
    }

    private static <E, R extends RootAware<E>, U> SubQuerying<E, R, U> of(
            RootSource<E, R> subRootSource,
            Mapper<E, R, U> mapper,
            SubQueryFilter<E, R> filter) {

        return new SubQuerying<>() {
            @Override
            public SubQuerying<E, R, U> filter(Filter<E, R> f) {
                return SubQuerying.of(rootSource(), mapper(), filter().and(SubQueryFilter.of(f)));
            }
            @Override
            public <E1, R1 extends RootAware<E1>> SubQuerying<E, R, U> filter(R1 correlateRoot, BiFilter<E1, R1, E, R> biFilter) {
                @SuppressWarnings("unchecked")
                Root<E1> raw = (Root<E1>) QueryContext.root();
                return SubQuerying.of(rootSource(), mapper(), filter().and(SubQueryFilter.correlateOf(raw, correlateRoot, biFilter)));
            }
            @Override
            public SubQueryFilter<E, R> filter() { return filter; }
            @Override
            public RootSource<E, R> rootSource() { return subRootSource; }
            @Override
            public Mapper<E, R, U> mapper() { return mapper; }
        };

    }

}
