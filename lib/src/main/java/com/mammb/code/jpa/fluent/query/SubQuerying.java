package com.mammb.code.jpa.fluent.query;

import com.mammb.code.jpa.core.Criteria;
import com.mammb.code.jpa.core.QueryContext;
import com.mammb.code.jpa.core.RootAware;
import com.mammb.code.jpa.core.RootSource;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Predicate;

public interface SubQuerying<E, R extends RootAware<E>, U> {

    SubQuerying<E, R, U> filter(Filter<E, R> filter);
    <Y> SubQuerying<E, R, Y> map(Mapper<E, R, Y> mapper);

    RootSource<E, R> rootSource();
    Mapper<E, R, U> mapper();
    Filter<E, R> filter();


    default <U> Expression<U> toExpression(Class<U> resultType, Criteria.ExpressionSelector<E, R, U> selector) {
        return QueryHelper.subQuery(
            rootSource(),
            Mapper.subQuery(resultType, ExpressionSelector.of(selector)),
            filter());
    }


    default Predicate exists() {
        return QueryContext.builder().exists(QueryHelper.subQuery(rootSource(), mapper(), filter()));
    }


    static <E, R extends RootAware<E>> SubQuerying<E, R, E> of(RootSource<E, R> subRootSource) {
        return SubQuerying.of(subRootSource, Mapper.subQuery(), Filter.empty());
    }


    private static <E, R extends RootAware<E>, U> SubQuerying<E, R, U> of(
            RootSource<E, R> subRootSource,
            Mapper<E, R, U> mapper,
            Filter<E, R> filter) {
        return new SubQuerying<>() {
            @Override
            public SubQuerying<E, R, U> filter(Filter<E, R> f) { return SubQuerying.of(rootSource(), mapper(), filter().and(f)); }
            @Override
            public <Y> SubQuerying<E, R, Y> map(Mapper<E, R, Y> mapper) { return SubQuerying.of(rootSource(), mapper, filter()); }
            @Override
            public Filter<E, R> filter() { return filter; }
            @Override
            public RootSource<E, R> rootSource() { return subRootSource; }
            @Override
            public Mapper<E, R, U> mapper() { return mapper; }
        };

    }
}
