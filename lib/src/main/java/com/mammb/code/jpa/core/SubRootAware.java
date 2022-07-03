package com.mammb.code.jpa.core;

import jakarta.persistence.criteria.AbstractQuery;
import jakarta.persistence.criteria.Root;

import java.util.function.Supplier;

public interface SubRootAware<E> extends RootAware<E> {
}
