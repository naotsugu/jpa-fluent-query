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
package com.mammb.code.jpa.fluent.query;

import com.mammb.code.jpa.fluent.core.Criteria;
import com.mammb.code.jpa.fluent.core.RootAware;
import jakarta.persistence.criteria.Expression;
import java.util.List;

/**
 * The grouping for GROUP BY clause.
 * @param <E> the type of entity
 * @param <R> the type of root
 * @author Naotsugu Kobayashi
 */
public interface Grouping<E, R extends RootAware<E>> {

    /**
     * Creates the {@link Expression} list for the given {@link RootAware}.
     * @param root the {@link RootAware}
     * @return the {@link Expression} list
     */
    List<Expression<?>> apply(R root);


    /**
     * Get the empty {@link Grouping}.
     * @param <E> the type of entity
     * @param <R> the type of root
     * @return a empty {@link Grouping}
     */
    static <E, R extends RootAware<E>> Grouping<E, R> empty() {
        return root -> null;
    }


    /**
     * Create the {@link Grouping} for the given selector.
     * @param exp the expression selector
     * @param <E> the type of entity
     * @param <R> the type of root
     * @param <U> the type of result
     * @return the {@link Grouping}
     */
    static <E, R extends RootAware<E>, U> Grouping<E, R> by(
            Criteria.ExpressionSelector<E, R, U>  exp) {
        return root ->  List.of(exp.apply(root).get());
    }


    /**
     * Create the {@link Grouping} for the given selector.
     * @param exp1 the expression selector
     * @param exp2 the expression selector
     * @param <E> the type of entity
     * @param <R> the type of root
     * @param <U1> the type of result
     * @param <U2> the type of result
     * @return the {@link Grouping}
     */
    static <E, R extends RootAware<E>, U1, U2> Grouping<E, R> by(
            Criteria.ExpressionSelector<E, R, U1>  exp1,
            Criteria.ExpressionSelector<E, R, U2>  exp2) {
        return root ->  List.of(exp1.apply(root).get(), exp2.apply(root).get());
    }


    /**
     * Create the {@link Grouping} for the given selector.
     * @param exp1 the expression selector
     * @param exp2 the expression selector
     * @param exp3 the expression selector
     * @param <E> the type of entity
     * @param <R> the type of root
     * @param <U1> the type of result
     * @param <U2> the type of result
     * @param <U3> the type of result
     * @return the {@link Grouping}
     */
    static <E, R extends RootAware<E>, U1, U2, U3> Grouping<E, R> by(
            Criteria.ExpressionSelector<E, R, U1>  exp1,
            Criteria.ExpressionSelector<E, R, U2>  exp2,
            Criteria.ExpressionSelector<E, R, U3>  exp3) {
        return root ->  List.of(exp1.apply(root).get(), exp2.apply(root).get(), exp3.apply(root).get());
    }


    /**
     * Create the {@link Grouping} for the given selector.
     * @param exp1 the expression selector
     * @param exp2 the expression selector
     * @param exp3 the expression selector
     * @param exp4 the expression selector
     * @param <E> the type of entity
     * @param <R> the type of root
     * @param <U1> the type of result
     * @param <U2> the type of result
     * @param <U3> the type of result
     * @param <U4> the type of result
     * @return the {@link Grouping}
     */
    static <E, R extends RootAware<E>, U1, U2, U3, U4> Grouping<E, R> by(
            Criteria.ExpressionSelector<E, R, U1>  exp1,
            Criteria.ExpressionSelector<E, R, U2>  exp2,
            Criteria.ExpressionSelector<E, R, U3>  exp3,
            Criteria.ExpressionSelector<E, R, U4>  exp4) {
        return root ->  List.of(exp1.apply(root).get(), exp2.apply(root).get(), exp3.apply(root).get(),
                                exp4.apply(root).get());
    }


    /**
     * Create the {@link Grouping} for the given selector.
     * @param exp1 the expression selector
     * @param exp2 the expression selector
     * @param exp3 the expression selector
     * @param exp4 the expression selector
     * @param exp5 the expression selector
     * @param <E> the type of entity
     * @param <R> the type of root
     * @param <U1> the type of result
     * @param <U2> the type of result
     * @param <U3> the type of result
     * @param <U4> the type of result
     * @param <U5> the type of result
     * @return the {@link Grouping}
     */
    static <E, R extends RootAware<E>, U1, U2, U3, U4, U5> Grouping<E, R> by(
            Criteria.ExpressionSelector<E, R, U1>  exp1,
            Criteria.ExpressionSelector<E, R, U2>  exp2,
            Criteria.ExpressionSelector<E, R, U3>  exp3,
            Criteria.ExpressionSelector<E, R, U4>  exp4,
            Criteria.ExpressionSelector<E, R, U5>  exp5) {
        return root ->  List.of(exp1.apply(root).get(), exp2.apply(root).get(), exp3.apply(root).get(),
                                exp4.apply(root).get(), exp5.apply(root).get());
    }


    /**
     * Create the {@link Grouping} for the given selector.
     * @param exp1 the expression selector
     * @param exp2 the expression selector
     * @param exp3 the expression selector
     * @param exp4 the expression selector
     * @param exp5 the expression selector
     * @param exp6 the expression selector
     * @param <E> the type of entity
     * @param <R> the type of root
     * @param <U1> the type of result
     * @param <U2> the type of result
     * @param <U3> the type of result
     * @param <U4> the type of result
     * @param <U5> the type of result
     * @param <U6> the type of result
     * @return the {@link Grouping}
     */
    static <E, R extends RootAware<E>, U1, U2, U3, U4, U5, U6> Grouping<E, R> by(
            Criteria.ExpressionSelector<E, R, U1>  exp1,
            Criteria.ExpressionSelector<E, R, U2>  exp2,
            Criteria.ExpressionSelector<E, R, U3>  exp3,
            Criteria.ExpressionSelector<E, R, U4>  exp4,
            Criteria.ExpressionSelector<E, R, U5>  exp5,
            Criteria.ExpressionSelector<E, R, U6>  exp6) {
        return root ->  List.of(exp1.apply(root).get(), exp2.apply(root).get(), exp3.apply(root).get(),
                                exp4.apply(root).get(), exp5.apply(root).get(), exp6.apply(root).get());
    }

}
