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
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;

import java.util.Objects;

/**
 * QueryContext.
 * @author Naotsugu Kobayashi
 */
public interface QueryContext {

    ThreadLocal<QueryContext> threadLocal = ThreadLocal.withInitial(QueryContextImpl::new);

    static CriteriaBuilder put(CriteriaBuilder builder) {
        ((QueryContextImpl) threadLocal.get()).builder = Objects.requireNonNull(builder);
        return builder;
    }
    static <E> CriteriaQuery<E> put(CriteriaQuery<E> query) {
        ((QueryContextImpl) threadLocal.get()).query = Objects.requireNonNull(query);
        return query;
    }
    static <E> Root<E> put(Root<E> root) {
        ((QueryContextImpl) threadLocal.get()).root = Objects.requireNonNull(root);
        return root;
    }
    static CriteriaBuilder builder() {
        return Objects.requireNonNull(((QueryContextImpl) threadLocal.get()).builder);
    }
    static CriteriaQuery<?> query() {
        return Objects.requireNonNull(((QueryContextImpl) threadLocal.get()).query);
    }
    static Root<?> root() {
        return Objects.requireNonNull(((QueryContextImpl) threadLocal.get()).root);
    }
    static void close() {
        var context = (QueryContextImpl) threadLocal.get();
        context.builder = null;
        context.query = null;
    }

    class QueryContextImpl implements QueryContext {
        private CriteriaBuilder builder;
        private CriteriaQuery<?> query;
        private Root<?> root;
    }

}
