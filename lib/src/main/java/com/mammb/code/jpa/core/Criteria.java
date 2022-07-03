/*
 * Copyright 2019-2022 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.mammb.code.jpa.core;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Order;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Selection;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.regex.Pattern;

/**
 * The criteria utilities.
 * @author Naotsugu Kobayashi
 */
public class Criteria {

    public interface Selector<E, R extends RootAware<E>, U> {
        Criteria.AnyExpression<U, ? extends Selection<U>> apply(R root);
    }

    interface CommonType extends BuilderAware {}


    public static class AnyPath<E> implements AnyExpression<E, Path<E>>, CommonType {
        private final Supplier<Path<E>> path;
        private final CriteriaBuilder builder;
        public AnyPath(Supplier<Path<E>> path, CriteriaBuilder builder) {
            this.path = path;
            this.builder = builder;
        }
        @Override public Path<E> get() { return path.get(); }
        @Override public CriteriaBuilder builder() { return builder; }
    }

    public static class AnyExp<E> implements AnyExpression<E, Expression<E>>, CommonType {
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
            extends AnyPath<E> implements ComparableExpression<E, Path<E>>, CommonType {
        public ComparablePath(Supplier<Path<E>> path, CriteriaBuilder builder) {
            super(path, builder);
        }
    }

    public static class ComparableExp<E extends Comparable<? super E>>
            extends AnyExp<E> implements ComparableExpression<E, Expression<E>>, CommonType {
        public ComparableExp(Supplier<Expression<E>> expression, CriteriaBuilder builder) {
            super(expression, builder);
        }
    }

    public static class StringPath extends AnyPath<String> implements StringExpression<Path<String>>, CommonType {
        public StringPath(Supplier<Path<String>> path, CriteriaBuilder builder) {
            super(path, builder);
        }
    }

    public static class StringExp extends AnyExp<String> implements StringExpression<Expression<String>>, CommonType {
        public StringExp(Supplier<Expression<String>> expression, CriteriaBuilder builder) {
            super(expression, builder);
        }
    }

    public static class BooleanPath extends AnyPath<Boolean> implements BooleanExpression<Path<Boolean>>, CommonType {
        public BooleanPath(Supplier<Path<Boolean>> path, CriteriaBuilder builder) {
            super(path, builder);
        }
    }
    public static class BooleanExp extends AnyExp<Boolean> implements BooleanExpression<Expression<Boolean>>, CommonType {
        public BooleanExp(Supplier<Expression<Boolean>> expression, CriteriaBuilder builder) {
            super(expression, builder);
        }
    }

    public static class NumberPath<T extends Number> extends AnyPath<T> implements NumberExpression<T, Path<T>>, CommonType {
        public NumberPath(Supplier<Path<T>> path, CriteriaBuilder builder) {
            super(path, builder);
        }
    }

    public static class NumberExp<T extends Number> extends AnyExp<T> implements NumberExpression<T, Expression<T>>, CommonType {
        public NumberExp(Supplier<Expression<T>> expression, CriteriaBuilder builder) {
            super(expression, builder);
        }
    }


    public static class AnyCollectionExp<C extends Collection<?>, T extends Expression<C>> implements AnyCollectionExpression<C, T>, CommonType {
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
            extends AnyCollectionExp<C, T> implements CollectionExpression<E, C, T>, CommonType {
        public CollectionExp(Supplier<T> expression, CriteriaBuilder builder) {
            super(expression, builder);
        }
    }

    // ------------------------------------------------------------------------

    public interface AnyExpression<E, T extends Expression<E>> extends Supplier<T>, CommonType {
        T get();
        default Predicate eq(AnyExpression<E, T> y) { return builder().equal(get(), y.get()); }
        default Predicate eq(Expression<?> y) { return builder().equal(get(), y); }
        default Predicate eq(Object y) { return isEmpty(y) ? null : builder().equal(get(), y); }
        default Predicate ne(AnyExpression<E, T> y) { return builder().notEqual(get(), y.get()); }
        default Predicate ne(Expression<?> y) { return builder().notEqual(get(), y); }
        default Predicate ne(Object y) { return isEmpty(y) ? null : builder().notEqual(get(), y); }
        default Predicate isNull(Expression<?> x) { return builder().isNull(get()); }
        default Predicate nonNull() { return builder().isNotNull(get()); }
        default Order asc() { return builder().asc(get()); }
        default Order desc() { return builder().desc(get()); }
    }

    public interface ComparableExpression<E extends Comparable<? super E>, T extends Expression<E>>
            extends Supplier<T>, AnyExpression<E, T>, CommonType {
        T get();
        default Predicate gt(Expression<? extends E> y) { return builder().greaterThan(get(), y); }
        default Predicate gt(E y) { return isEmpty(y) ? null : builder().greaterThan(get(), y); }
        default Predicate ge(Expression<? extends E> y) { return builder().greaterThanOrEqualTo(get(), y); }
        default Predicate ge(E y) { return isEmpty(y) ? null : builder().greaterThanOrEqualTo(get(), y); }
        default Predicate lt(Expression<? extends E> y) { return builder().lessThan(get(), y); }
        default Predicate lt(E y) { return isEmpty(y) ? null : builder().lessThan(get(), y); }
        default Predicate le(Expression<? extends E> y) { return builder().lessThanOrEqualTo(get(), y); }
        default Predicate le(E y) { return isEmpty(y) ? null : builder().lessThanOrEqualTo(get(), y); }
        default Predicate between(Expression<? extends E> x, Expression<? extends E> y) { return builder().between(get(), x, y); }
        default Predicate between(E x, E y) {
            return (isEmpty(x) && isEmpty(y)) ? null
                : isEmpty(y) ? ge(x)
                : isEmpty(x) ? le(y)
                : builder().between(get(), x, y);
        }
    }

    public interface StringExpression<T extends Expression<String>>
            extends Supplier<T>, AnyExpression<String, T>, ComparableExpression<String, T>, CommonType {
        T get();
        default Predicate like(Expression<String> pattern) { return builder().like(get(), pattern, '\\'); }
        default Predicate like(String pattern) { return isEmpty(pattern) ? null : builder().like(get(), escaped(pattern), '\\'); }
        default Predicate likePartial(String pattern) { return isEmpty(pattern) ? null : builder().like(get(), escapedPartial(pattern), '\\'); }
        default Predicate notLike(Expression<String> pattern) { return builder().notLike(get(), pattern); }
        default Predicate notLike(String pattern) { return isEmpty(pattern) ? null : builder().notLike(get(), escaped(pattern), '\\'); }
        default Predicate notLikePartial(String pattern) { return isEmpty(pattern) ? null : builder().notLike(get(), escapedPartial(pattern), '\\'); }

        Pattern ESCAPE_PATTERN = Pattern.compile("([%_\\\\])");
        private static String escaped(String str) {
            return ESCAPE_PATTERN.matcher(str).replaceAll("\\\\$1") + "%";
        }
        private static String escapedPartial(String str) {
            return "%" + ESCAPE_PATTERN.matcher(str).replaceAll("\\\\$1") + "%";
        }

        default Expression<String> concat(String y) { return builder().concat(get(), y); }
        default Expression<String> concat(Expression<String> y) { return builder().concat(get(), y); }
        default Expression<String> substring(int from) { return builder().substring(get(), from); }
        default Expression<String> substring(int from, int len) { return builder().substring(get(), from, len); }
        default Expression<String> trim() { return builder().trim(get()); }
        default Expression<String> lower() { return builder().lower(get()); }
        default Expression<String> upper() { return builder().upper(get()); }
        default Expression<Integer> length() { return builder().length(get()); }
    }

    public interface BooleanExpression<T extends Expression<Boolean>>
            extends Supplier<T>, AnyExpression<Boolean, T>, ComparableExpression<Boolean, T>, CommonType {
        T get();
        default Predicate isTrue() { return builder().isTrue(get()); }
        default Predicate isFalse() { return builder().isFalse(get()); }
    }

    public interface NumberExpression<E extends Number, T extends Expression<E>>
            extends Supplier<T>, AnyExpression<E, T>, CommonType {
        T get();
        default Predicate gt(Expression<? extends Number> y) { return builder().gt(get(), y); }
        default Predicate gt(Number y) { return Objects.isNull(y) ? null : builder().gt(get(), y); }
        default Predicate ge(Expression<? extends Number> y) { return builder().ge(get(), y); }
        default Predicate ge(Number y) { return Objects.isNull(y) ? null : builder().ge(get(), y); }
        default Predicate lt(Expression<? extends Number> y) { return builder().lt(get(), y); }
        default Predicate lt(Number y) { return Objects.isNull(y) ? null : builder().lt(get(), y); }
        default Predicate le(Expression<? extends Number> y) { return builder().le(get(), y); }
        default Predicate le(Number y) { return Objects.isNull(y) ? null : builder().le(get(), y); }

        default Expression<Long> toLong() { return builder().toLong(get()); }
        default Expression<Integer> toInteger() { return builder().toInteger(get()); }
        default Expression<Float> toFloat() { return builder().toFloat(get()); }
        default Expression<Double> toDouble() { return builder().toDouble(get()); }
        default Expression<BigDecimal> toBigDecimal() { return builder().toBigDecimal(get()); }
        default Expression<BigInteger> toBigInteger() { return builder().toBigInteger(get()); }
    }

    public interface AnyCollectionExpression<C extends Collection<?>, T extends Expression<C>>
            extends Supplier<T>, AnyExpression<C, T>, CommonType {
        T get();
        default Predicate isEmpty() { return builder().isEmpty(get()); }
        default Predicate isNotEmpty() { return builder().isNotEmpty(get()); }
        default Expression<Integer> size(Expression<C> collection) { return builder().size(get()); }
    }

    public interface CollectionExpression<E, C extends Collection<E>, T extends Expression<C>>
            extends Supplier<T>, AnyExpression<C, T>, AnyCollectionExpression<C, T>, CommonType {
        T get();
        default Predicate isMember(Expression<E> elem) { return builder().isMember(elem, get()); }
        default Predicate isMember(E elem) { return Objects.isNull(elem) ? null : builder().isMember(elem, get()); }
        default Predicate isNotMember(Expression<E> elem) { return builder().isMember(elem, get()); }
        default Predicate isNotMember(E elem) { return Objects.isNull(elem) ? null : builder().isMember(elem, get()); }
    }

    public interface AnyMapExpression<M extends Map<?, ?>, T extends Expression<M>>
        extends Supplier<T>, AnyExpression<M, T>, CommonType {
        T get();
    }

    public interface MapExpression<K, V, M extends Map<K, V>, T extends Expression<M>>
        extends Supplier<T>, AnyExpression<M, T>, AnyMapExpression<M, T>, CommonType {
        T get();
    }

    public interface MapKeyPath<K> extends Supplier<Path<K>>, CommonType {
        Path<K> key();
    }
    public interface MapValuePath<V> extends Supplier<Path<V>>, CommonType {
        Path<V> key();
    }

    private static boolean isEmpty(Object obj) {
        return Objects.isNull(obj) || (obj instanceof String str && str.isEmpty());
    }

}
