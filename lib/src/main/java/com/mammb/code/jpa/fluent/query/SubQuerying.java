package com.mammb.code.jpa.fluent.query;

import com.mammb.code.jpa.core.Criteria;
import com.mammb.code.jpa.core.QueryContext;
import com.mammb.code.jpa.core.RootAware;
import com.mammb.code.jpa.core.RootSource;
import jakarta.persistence.criteria.AbstractQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Predicate;

public interface SubQuerying<E, R extends RootAware<E>, U> {

    SubQuerying<E, R, U> filter(Filter<E, R> filter);

    RootSource<E, R> rootSource();
    Mapper<E, R, U> mapper();
    Filter<E, R> filter();


    default <U> Expression<U> to(Class<U> resultType, Criteria.ExpressionSelector<E, R, U> selector) {
        return QueryHelper.subQuery(rootSource(), filter(),
            Mapper.subQuery(resultType, ExpressionSelector.of(selector)));
    }

    default Expression<Long> count() {
        return QueryContext.builder().count(QueryHelper.subQuery(rootSource(), filter(), mapper()));
    }

    default Predicate exists() {
        return QueryContext.builder().exists(QueryHelper.subQuery(rootSource(), filter(), mapper()));
    }

    static <E, R extends RootAware<E>> SubQuerying<E, R, E> of(RootSource<E, R> subRootSource) {
        return SubQuerying.of(subRootSource, Mapper.subQuery(), Filter.empty());
    }

    static <E, R extends RootAware<E>> SubQuerying<E, R, E> of(R correlate) {
        return SubQuerying.of(RootSource.directly(correlate, correlate.type()), Mapper.correlate(), Filter.empty());
    }

    private static <E, R extends RootAware<E>, U> SubQuerying<E, R, U> of(
            RootSource<E, R> subRootSource,
            Mapper<E, R, U> mapper,
            Filter<E, R> filter) {
        return new SubQuerying<>() {
            @Override
            public SubQuerying<E, R, U> filter(Filter<E, R> f) { return SubQuerying.of(rootSource(), mapper(), filter().and(f)); }
            @Override
            public Filter<E, R> filter() { return filter; }
            @Override
            public RootSource<E, R> rootSource() { return subRootSource; }
            @Override
            public Mapper<E, R, U> mapper() { return mapper; }
        };

    }
}
