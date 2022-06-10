package com.mammb.code.jpa.core;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import java.util.Collection;
import java.util.function.Supplier;

public class Criteria {

    public static class AnyPath<E> implements AnyExpression<E, Path<E>>, Builder {
        private final Supplier<Path<E>> path;
        private final CriteriaBuilder builder;
        public AnyPath(Supplier<Path<E>> path, CriteriaBuilder builder) {
            this.path = path;
            this.builder = builder;
        }
        @Override public Path<E> get() { return path.get(); }
        @Override public CriteriaBuilder builder() { return builder; }
    }

    public static class AnyExp<E> implements AnyExpression<E, Expression<E>>, Builder {
        private final Supplier<Expression<E>> expression;
        private final CriteriaBuilder builder;
        public AnyExp(Supplier<Expression<E>> expression, CriteriaBuilder builder) {
            this.expression = expression;
            this.builder = builder;
        }
        @Override public Expression<E> get() { return expression.get(); };
        @Override public CriteriaBuilder builder() { return builder; }
    }

    public static class ComparablePath<E extends Comparable<? super E>>
            extends AnyPath<E> implements ComparableExpression<E, Path<E>>, Builder {
        public ComparablePath(Supplier<Path<E>> path, CriteriaBuilder builder) {
            super(path, builder);
        }
    }

    public static class ComparableExp<E extends Comparable<? super E>>
            extends AnyExp<E> implements ComparableExpression<E, Expression<E>>, Builder {
        public ComparableExp(Supplier<Expression<E>> expression, CriteriaBuilder builder) {
            super(expression, builder);
        }
    }

    public static class StringPath extends AnyPath<String> implements StringExpression<Path<String>>, Builder {
        public StringPath(Supplier<Path<String>> path, CriteriaBuilder builder) {
            super(path, builder);
        }
    }

    public static class StringExp extends AnyExp<String> implements StringExpression<Expression<String>>, Builder {
        public StringExp(Supplier<Expression<String>> expression, CriteriaBuilder builder) {
            super(expression, builder);
        }
    }

    public static class BooleanPath extends AnyPath<Boolean> implements BooleanExpression<Path<Boolean>>, Builder {
        public BooleanPath(Supplier<Path<Boolean>> path, CriteriaBuilder builder) {
            super(path, builder);
        }
    }
    public static class BooleanExp extends AnyExp<Boolean> implements BooleanExpression<Expression<Boolean>>, Builder {
        public BooleanExp(Supplier<Expression<Boolean>> expression, CriteriaBuilder builder) {
            super(expression, builder);
        }
    }

    public static class NumberPath<T extends Number> extends AnyPath<T> implements NumberExpression<T, Path<T>>, Builder {
        public NumberPath(Supplier<Path<T>> path, CriteriaBuilder builder) {
            super(path, builder);
        }
    }

    public static class NumberExp<T extends Number> extends AnyExp<T> implements NumberExpression<T, Expression<T>>, Builder {
        public NumberExp(Supplier<Expression<T>> expression, CriteriaBuilder builder) {
            super(expression, builder);
        }
    }


    public static class AnyCollectionExp<C extends Collection<?>, T extends Expression<C>> implements AnyCollectionExpression<C, T>, Builder {
        private final Supplier<T> expression;
        private final CriteriaBuilder builder;
        public AnyCollectionExp(Supplier<T> expression, CriteriaBuilder builder) {
            this.expression = expression;
            this.builder = builder;
        }
        @Override public T get() { return expression.get(); }
        @Override public CriteriaBuilder builder() { return builder; }
    }

    public static class CollectionExp<E, C extends Collection<E>, T extends Expression<C>>
            extends AnyCollectionExp<C, T> implements CollectionExpression<E, C, T>, Builder {
        public CollectionExp(Supplier<T> expression, CriteriaBuilder builder) {
            super(expression, builder);
        }
    }

    // ------------------------------------------------------------------------

    public interface AnyExpression<E, T extends Expression<E>> extends Supplier<T>, Builder {
        T get();
        default Predicate eq(Expression<?> y) {
            return builder().equal(get(), y);
        }
        default Predicate eq(Object y) {
            return builder().equal(get(), y);
        }
        default Predicate ne(Expression<?> y) {
            return builder().notEqual(get(), y);
        }
        default Predicate ne(Object y) {
            return builder().notEqual(get(), y);
        }
        default Predicate isNull(Expression<?> x) {
            return builder().isNull(get());
        }
        default Predicate nonNull() {
            return builder().isNotNull(get());
        }
    }

    public interface ComparableExpression<E extends Comparable<? super E>, T extends Expression<E>>
            extends Supplier<T>, AnyExpression<E, T> {
        T get();
        default Predicate gt(Expression<? extends E> y) { return builder().greaterThan(get(), y); }
        default Predicate gt(E y) { return builder().greaterThan(get(), y); }
        default Predicate ge(Expression<? extends E> y) { return builder().greaterThanOrEqualTo(get(), y); }
        default Predicate ge(E y) { return builder().greaterThanOrEqualTo(get(), y); }
        default Predicate lt(Expression<? extends E> y) { return builder().lessThan(get(), y); }
        default Predicate lt(E y) { return builder().lessThan(get(), y); }
        default Predicate le(Expression<? extends E> y) { return builder().lessThanOrEqualTo(get(), y); }
        default Predicate le(E y) { return builder().lessThanOrEqualTo(get(), y); }
        default Predicate between(Expression<? extends E> x, Expression<? extends E> y) { return builder().between(get(), x, y); }
        default Predicate between(E x, E y) { return builder().between(get(), x, y); }
    }

    public interface StringExpression<T extends Expression<String>>
            extends Supplier<T>, AnyExpression<String, T>, ComparableExpression<String, T> {
        T get();
        default Predicate like(Expression<String> pattern) { return builder().like(get(), pattern); }
        default Predicate like(String pattern) { return builder().like(get(), pattern); }
        default Predicate like(Expression<String> pattern, char escapeChar) { return builder().like(get(), pattern, escapeChar); }
        default Predicate like(String pattern, char escapeChar) { return builder().like(get(), pattern, escapeChar); }
        default Predicate notLike(Expression<String> pattern) { return builder().notLike(get(), pattern); }
        default Predicate notLike(String pattern) { return builder().notLike(get(), pattern); }
        default Predicate notLike(Expression<String> pattern, char escapeChar) { return builder().notLike(get(), pattern, escapeChar); }
        default Predicate notLike(String pattern, char escapeChar) { return builder().notLike(get(), pattern, escapeChar); }
    }

    public interface BooleanExpression<T extends Expression<Boolean>>
            extends Supplier<T>, AnyExpression<Boolean, T>, ComparableExpression<Boolean, T> {
        T get();
        default Predicate isTrue() { return builder().isTrue(get()); }
        default Predicate isFalse() { return builder().isFalse(get()); }
    }

    public interface NumberExpression<E extends Number, T extends Expression<E>>
            extends Supplier<T>, AnyExpression<E, T> {
        T get();
        default Predicate gt(Expression<? extends Number> y) { return builder().gt(get(), y); }
        default Predicate gt(Number y) { return builder().gt(get(), y); }
        default Predicate ge(Expression<? extends Number> y) { return builder().ge(get(), y); }
        default Predicate ge(Number y) { return builder().ge(get(), y); }
        default Predicate lt(Expression<? extends Number> y) { return builder().lt(get(), y); }
        default Predicate lt(Number y) { return builder().lt(get(), y); }
        default Predicate le(Expression<? extends Number> y) { return builder().le(get(), y); }
        default Predicate le(Number y) { return builder().le(get(), y); }
    }

    public interface AnyCollectionExpression<C extends Collection<?>, T extends Expression<C>>
            extends Supplier<T>, AnyExpression<C, T> {
        T get();
        default Predicate isEmpty() { return builder().isEmpty(get()); }
        default Predicate isNotEmpty() { return builder().isNotEmpty(get()); }
        default Expression<Integer> size(Expression<C> collection) { return builder().size(get()); }
    }

    public interface CollectionExpression<E, C extends Collection<E>, T extends Expression<C>>
            extends Supplier<T>, AnyExpression<C, T>, AnyCollectionExpression<C, T> {
        T get();
        default Predicate isMember(Expression<E> elem) { return builder().isMember(elem, get()); }
        default Predicate isMember(E elem) { return builder().isMember(elem, get()); }
        default Predicate isNotMember(Expression<E> elem) { return builder().isMember(elem, get()); }
        default Predicate isNotMember(E elem) { return builder().isMember(elem, get()); }
    }

    public interface MapKeyPath<K> extends Supplier<Path<K>> {
        Path<K> key();
    }
    public interface MapValuePath<V> extends Supplier<Path<V>> {
        Path<V> key();
    }

}
