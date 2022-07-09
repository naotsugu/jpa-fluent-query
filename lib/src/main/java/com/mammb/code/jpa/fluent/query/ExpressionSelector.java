package com.mammb.code.jpa.fluent.query;

import com.mammb.code.jpa.core.Criteria;
import com.mammb.code.jpa.core.RootAware;
import jakarta.persistence.criteria.Expression;

public interface ExpressionSelector<E, R extends RootAware<E>, U> {

    Expression<U> apply(R root);

    static <E, R extends RootAware<E>, U> ExpressionSelector<E, R, U> of(Criteria.ExpressionSelector<E, R, U> source) {
        return r -> source.apply(r).get();
    }

}
