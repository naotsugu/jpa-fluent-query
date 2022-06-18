package com.mammb.code.jpa.core;

import java.io.Serializable;

public interface BaseRepository<PK extends Serializable, E, R extends RootAware<E>> {
    RootSource<E, R> rootSource();
}
