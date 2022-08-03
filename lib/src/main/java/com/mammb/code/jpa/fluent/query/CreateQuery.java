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

import com.mammb.code.jpa.fluent.core.RootAware;
import com.mammb.code.jpa.fluent.core.RootSource;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * Create Query helper interface.
 * @param <E> the type of entity of root
 * @param <R> the type of root element
 * @param <U> the type of query result
 * @author Naotsugu Kobayashi
 */
public interface CreateQuery<E, R extends RootAware<E>, U> {

    /**
     * Get the root source.
     * @return the root source
     */
    RootSource<E, R> rootSource();


    /**
     * Get the current filter.
     * @return the current filter
     */
    Filter<E, R> filter();


    /**
     * Get the current sorts.
     * @return the current sorts
     */
    Sorts<E, R> sorts();


    /**
     * Get the current mapper.
     * @return the current mapper
     */
    Mapper<E, R, U> mapper();


    /**
     * Get the current hints.
     * @return the current hints
     */
    Hints hints();


    /**
     * Get the count result.
     * @return the count result
     */
    default Query<Long> count() {
        return em -> QueryBuilder.countQuery(em, rootSource(), filter(), hints())
            .getSingleResult();
    }


    /**
     * Get the count result.
     * @param request The request
     * @return the count result
     */
    default Query<Long> count(Request<E, R> request) {
        return em -> QueryBuilder.countQuery(em, rootSource(), filter().and(request.getFilter()), hints())
            .getSingleResult();
    }


    /**
     * Get the optional single result.
     * @return the optional single result
     */
    default Query<Optional<U>> toOptional() {
        return em -> Optional.ofNullable(
            QueryBuilder.query(em, rootSource(), mapper(), filter(), Sorts.empty(), hints())
                .getSingleResult());
    }


    /**
     * Get the optional single result.
     * @param request The request
     * @return the optional single result
     */
    default Query<Optional<U>> toOptional(Request<E, R> request) {
        return em -> Optional.ofNullable(
            QueryBuilder.query(em, rootSource(), mapper(), filter().and(request.getFilter()), Sorts.empty(), hints())
                .getSingleResult());
    }


    /**
     * Get the single result.
     * @return the single result
     */
    default Query<U> toSingle() {
        return em -> QueryBuilder.query(em, rootSource(), mapper(), filter(), Sorts.empty(), hints())
            .getSingleResult();
    }


    /**
     * Get the single result.
     * @param request The request
     * @return the single result
     */
    default Query<U> toSingle(Request<E, R> request) {
        return em -> QueryBuilder.query(em, rootSource(), mapper(), filter().and(request.getFilter()), Sorts.empty(), hints())
            .getSingleResult();
    }


    /**
     * Get the {@link List} result.
     * @return the {@link List} result
     */
    default Query<List<U>> toList() {
        return em -> QueryBuilder.query(em, rootSource(), mapper(), filter(), sorts(), hints()).getResultList();
    }


    /**
     * Get the {@link List} result.
     * @param request The request
     * @return the {@link List} result
     */
    default Query<List<U>> toList(Request<E, R> request) {
        return em -> QueryBuilder.query(em, rootSource(), mapper(),
            filter().and(request.getFilter()), sorts().ands(request.getSorts()), hints()).getResultList();
    }


    /**
     * Get the {@link Slice} result.
     * @param slicePoint the slice point
     * @return the {@link Slice} result
     */
    default Query<Slice<U>> toSlice(SlicePoint slicePoint) {
        return em -> QueryBuilder.slice(em, rootSource(), mapper(), filter(), sorts(), slicePoint, hints());
    }


    /**
     * Get the {@link Slice} result.
     * @param request The slice request
     * @return the {@link Slice} result
     */
    default Query<Slice<U>> toSlice(SliceRequest<E, R> request) {
        return em -> QueryBuilder.slice(em, rootSource(), mapper(),
            filter().and(request.getFilter()), sorts().ands(request.getSorts()), request.getSlicePoint(), hints());
    }


    /**
     * Get the {@link Page} result.
     * @param slicePoint the slice point
     * @return the {@link Page} result
     */
    default Query<Page<U>> toPage(SlicePoint slicePoint) {
        return em -> QueryBuilder.page(em, rootSource(), mapper(), filter(), sorts(), slicePoint, hints());
    }


    /**
     * Get the {@link Page} result.
     * @param request The slice request
     * @return the {@link Page} result
     */
    default Query<Page<U>> toPage(SliceRequest<E, R> request) {
        return em -> QueryBuilder.page(em, rootSource(), mapper(),
            filter().and(request.getFilter()), sorts().ands(request.getSorts()), request.getSlicePoint(), hints());
    }


    /**
     * Get the {@link Stream} result.
     * This Stream reads records by page.
     * This {@link Stream} is descending order.
     * @return the {@link Stream} result
     */
    default Query<Stream<U>> toStream() {
        return toStream(100);
    }


    /**
     * Get the {@link Stream} result.
     * This Stream reads records by page.
     * This {@link Stream} is descending order.
     * @param pageSize The size of page
     * @return the {@link Stream} result
     */
    default Query<Stream<U>> toStream(int pageSize) {
        return em -> SliceStream.of(
            QueryBuilder.countQuery(em, rootSource(), filter(), hints()),
            QueryBuilder.query(em, rootSource(), mapper(), filter(), sorts(), hints()),
            pageSize
        ).stream();
    }


    /**
     * Get the {@link Stream} result.
     * This Stream reads records by page.
     * This {@link Stream} is descending order.
     * @param request The slice request
     * @return the {@link Stream} result
     */
    default Query<Stream<U>> toStream(SliceRequest<E, R> request) {
        return em -> SliceStream.of(
            QueryBuilder.countQuery(em, rootSource(), filter().and(request.getFilter()), hints()),
            QueryBuilder.query(em, rootSource(), mapper(), filter().and(request.getFilter()), sorts().ands(request.getSorts()), hints()),
            request.getSize()
        ).stream();
    }

    /**
     * Get the {@link Stream} result.
     * This Stream reads records by page.
     * It is recommended that {@code toStream()} be used when updating a record that has already been read,
     * since there is a possibility of missing processing.
     * @return the {@link Stream} result
     */
    default Query<Stream<U>> toForwardingStream() {
        return toForwardingStream(100);
    }


    /**
     * Get the {@link Stream} result.
     * This Stream reads records by page.
     * It is recommended that {@code toStream(int pageSize)} be used
     * when updating a record that has already been read,
     * since there is a possibility of missing processing.
     * @param pageSize The size of page
     * @return the {@link Stream} result
     */
    default Query<Stream<U>> toForwardingStream(int pageSize) {
        return em -> SliceStream.forwardOf(
            QueryBuilder.query(em, rootSource(), mapper(), filter(), sorts(), hints()),
            pageSize
        ).stream();
    }


    /**
     * Get the {@link Stream} result.
     * This Stream reads records by page.
     * It is recommended that {@code toStream(SliceRequest<E, R> request)} be used
     * when updating a record that has already been read,
     * since there is a possibility of missing processing.
     * @param request The slice request
     * @return the {@link Stream} result
     */
    default Query<Stream<U>> toForwardingStream(SliceRequest<E, R> request) {
        return em -> SliceStream.forwardOf(
            QueryBuilder.query(em, rootSource(), mapper(), filter().and(request.getFilter()), sorts().ands(request.getSorts()), hints()),
            request.getSize()
        ).stream();
    }


    /**
     * Get the {@link Stream} result.
     * This Stream reads records by page.
     * This {@link Stream} is descending order.
     * @return the {@link Stream} result
     */
    default Query<Iterable<U>> toIterable() {
        return toIterable(100);
    }


    /**
     * Get the {@link Iterable} result.
     * This Iterable reads records by page.
     * This {@link Iterable} is descending order.
     * @param pageSize The size of page
     * @return the {@link Iterable} result
     */
    default Query<Iterable<U>> toIterable(int pageSize) {
        return em -> SliceStream.of(
            QueryBuilder.countQuery(em, rootSource(), filter(), hints()),
            QueryBuilder.query(em, rootSource(), mapper(), filter(), sorts(), hints()),
            pageSize
        );
    }


    /**
     * Get the {@link Iterable} result.
     * This Iterable reads records by page.
     * This {@link Iterable} is descending order.
     * @param request The slice request
     * @return the {@link Iterable} result
     */
    default Query<Iterable<U>> toIterable(SliceRequest<E, R> request) {
        return em -> SliceStream.of(
            QueryBuilder.countQuery(em, rootSource(), filter(), hints()),
            QueryBuilder.query(em, rootSource(), mapper(), filter().and(request.getFilter()), sorts().ands(request.getSorts()), hints()),
            request.getSize()
        );
    }


    /**
     * Get the {@link Iterable} result.
     * This Iterable reads records by page.
     * It is recommended that {@code toIterable()} be used when updating a record that has already been read,
     * since there is a possibility of missing processing.
     * @return the {@link Iterable} result
     */
    default Query<Iterable<U>> toForwardingIterable() {
        return toForwardingIterable(100);
    }


    /**
     * Get the {@link Iterable} result.
     * This Iterable reads records by page.
     * It is recommended that {@code toIterable(int pageSize)} be used
     * when updating a record that has already been read,
     * since there is a possibility of missing processing.
     * @param pageSize The size of page
     * @return the {@link Iterable} result
     */
    default Query<Iterable<U>> toForwardingIterable(int pageSize) {
        return em -> SliceStream.forwardOf(
            QueryBuilder.query(em, rootSource(), mapper(), filter(), sorts(), hints()),
            pageSize
        );
    }


    /**
     * Get the {@link Iterable} result.
     * This Iterable reads records by page.
     * It is recommended that {@code toIterable(SliceRequest<E, R> request)} be used
     * when updating a record that has already been read,
     * since there is a possibility of missing processing.
     * @param request The slice request
     * @return the {@link Iterable} result
     */
    default Query<Iterable<U>> toForwardingIterable(SliceRequest<E, R> request) {
        return em -> SliceStream.forwardOf(
            QueryBuilder.query(em, rootSource(), mapper(), filter().and(request.getFilter()), sorts().ands(request.getSorts()), hints()),
            request.getSize()
        );
    }

}
