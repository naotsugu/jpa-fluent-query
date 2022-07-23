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

import jakarta.persistence.TypedQuery;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * The {@link SliceStream} is supports streaming loading.
 * @param <U> The type of query result.
 */
public class SliceStream<U> implements Iterator<U>, Iterable<U> {

    private final TypedQuery<Long> countQuery;
    private final TypedQuery<U> query;
    private final Deque<U> deque;
    private final List<Runnable> perActions;

    private SlicePoint slicePoint;
    private Boolean hasNextPage;


    private SliceStream(TypedQuery<Long> countQuery, TypedQuery<U> query, SlicePoint slicePoint,
            Deque<U> deque, List<Runnable> perActions) {
        this.countQuery = countQuery;
        this.query = Objects.requireNonNull(query);
        this.slicePoint = Objects.requireNonNull(slicePoint);
        this.deque = Objects.requireNonNull(deque);
        this.perActions = Objects.requireNonNull(perActions);
    }


    /**
     * Create the {@link SliceStream}.
     * @param countQuery The count query
     * @param query The query
     * @param pageSize The size of page
     * @param <U> The type of query result
     * @return The {@link SliceStream}
     */
    public static <U> SliceStream<U> of(TypedQuery<Long> countQuery, TypedQuery<U> query, int pageSize) {
        return new SliceStream<>(countQuery, query, SlicePoint.of(-1, pageSize), new ArrayDeque<>(), new ArrayList<>());
    }


    /**
     * Create the {@link SliceStream}.
     * @param query The query
     * @param pageSize The size of page
     * @param <U> The type of query result
     * @return The {@link SliceStream}
     */
    public static <U> SliceStream<U> forwardOf(TypedQuery<U> query, int pageSize) {
        return new SliceStream<>(null, query, SlicePoint.of(-1, pageSize), new ArrayDeque<>(), new ArrayList<>());
    }


    /**
     * Add actions to be performed on a page-by-page.
     * @param action the actions to be performed
     */
    public void addPerAction(Runnable action) {
        perActions.add(Objects.requireNonNull(action));
    }


    /**
     * Get the {@link Stream}.
     * @return the {@link Stream}
     */
    public Stream<U> stream() {
        final Iterable<U> iterable = () -> this;
        return StreamSupport.stream(iterable.spliterator(), false);
    }


    @Override
    public Iterator<U> iterator() {
        return this;
    }


    @Override
    public boolean hasNext() {
        if (Objects.isNull(hasNextPage) || hasNextPage && deque.isEmpty()) {
            refuel();
        }
        return hasNextPage || !deque.isEmpty();
    }


    @Override
    public U next() {
        return isForward() ? deque.poll() : deque.pollLast();
    }


    private boolean isForward() {
        return Objects.isNull(countQuery);
    }


    private void refuel() {
        perActions.forEach(Runnable::run);
        if (isForward()) {
            refuelForward();
        } else {
            refuelBackward();
        }
    }


    private void refuelForward() {
        slicePoint = slicePoint.next();
        Slice<U> slice = QueryBuilder.slice(query, slicePoint);
        hasNextPage = slice.hasNext();
        deque.addAll(slice.getContent());
    }


    private void refuelBackward() {
        if (slicePoint.getNumber() == 0) {
            throw new RuntimeException();
        }
        if (slicePoint.getNumber() < 0) {
            long count = countQuery.getSingleResult();
            if (count <= 0) {
                hasNextPage = false;
                return;
            }
            long lastPage = count / slicePoint.getSize();
            slicePoint = slicePoint.withNumber(Math.toIntExact(lastPage));
        } else {
            slicePoint = slicePoint.withNumber(slicePoint.getNumber() - 1);
        }

        Slice<U> slice = QueryBuilder.slice(query, slicePoint);
        hasNextPage = slicePoint.getNumber() > 0;
        deque.addAll(slice.getContent());
    }

}
