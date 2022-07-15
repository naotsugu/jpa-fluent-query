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

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import java.util.Objects;

/**
 * QueryContext.
 * Used to propagate context to subqueries.
 * @author Naotsugu Kobayashi
 */
public interface QueryContext {

    /** The context holder. */
    ThreadLocal<QueryContext> threadLocal = ThreadLocal.withInitial(QueryContextImpl::new);


    /**
     * Put the {@link CriteriaBuilder} to this context.
     * @param builder the {@link CriteriaBuilder} to be put
     * @return a current {@link CriteriaBuilder}
     */
    static CriteriaBuilder put(CriteriaBuilder builder) {
        ((QueryContextImpl) threadLocal.get()).builder = Objects.requireNonNull(builder);
        return builder;
    }


    /**
     * Put the {@link CriteriaQuery} to this context.
     * @param query the {@link CriteriaQuery} to be put
     * @param <E> the type of the query result
     * @return a current {@link CriteriaQuery}
     */
    static <E> CriteriaQuery<E> put(CriteriaQuery<E> query) {
        ((QueryContextImpl) threadLocal.get()).query = Objects.requireNonNull(query);
        return query;
    }


    /**
     * Put the {@link Root} to this context.
     * @param root the {@link Root} to be put
     * @param <E> the type of entity root
     * @return a current {@link Root}
     */
    static <E> Root<E> put(Root<E> root) {
        ((QueryContextImpl) threadLocal.get()).root = Objects.requireNonNull(root);
        return root;
    }


    /**
     * Get a {@link CriteriaBuilder} on current context.
     * @return a {@link CriteriaBuilder} on current context
     */
    static CriteriaBuilder builder() {
        return Objects.requireNonNull(((QueryContextImpl) threadLocal.get()).builder);
    }


    /**
     * Get a {@link CriteriaQuery} on current context.
     * @return a {@link CriteriaQuery} on current context
     */
    static CriteriaQuery<?> query() {
        return Objects.requireNonNull(((QueryContextImpl) threadLocal.get()).query);
    }


    /**
     * Get a {@link Root} on current context.
     * @param <E> the type of entity root
     * @return a {@link Root} on current context
     */
    @SuppressWarnings("unchecked")
    static <E> Root<E> root() {
        return Objects.requireNonNull((Root<E>) ((QueryContextImpl) threadLocal.get()).root);
    }


    /**
     * Close the current context.
     */
    static void close() {
        var context = (QueryContextImpl) threadLocal.get();
        context.builder = null;
        context.query = null;
    }


    /**
     * The query context holder.
     */
    class QueryContextImpl implements QueryContext {
        private CriteriaBuilder builder;
        private CriteriaQuery<?> query;
        private Root<?> root;
        private QueryContextImpl() { }
    }

}
