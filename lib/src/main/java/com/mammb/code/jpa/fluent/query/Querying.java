package com.mammb.code.jpa.fluent.query;

import com.mammb.code.jpa.core.RootAware;
import com.mammb.code.jpa.core.RootSource;
import com.mammb.code.jpa.fluent.query.impl.QueryingImpl;
import java.util.List;

public interface Querying<E, R extends RootAware<E>> {

    Querying<E, R> filter(Filter<E, R> filter);

    Query<List<E>> toList();

    static <E, R extends RootAware<E>> Querying<E, R> of(RootSource<E, R> roots) {
        return new QueryingImpl<>(roots);
    }
}
