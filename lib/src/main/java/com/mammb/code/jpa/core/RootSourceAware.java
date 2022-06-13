package com.mammb.code.jpa.core;

public interface RootSourceAware<E, R extends RootAware<E>> {
    RootSource<E, R> rootSource();
}
