package com.mammb.code.jpa.core;

import jakarta.persistence.criteria.Root;
import java.util.function.Supplier;

public interface RootAware<E> extends Supplier<Root<E>>, Builder {
}
