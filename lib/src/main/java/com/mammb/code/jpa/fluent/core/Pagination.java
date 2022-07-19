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
package com.mammb.code.jpa.fluent.core;

import com.mammb.code.jpa.fluent.query.Page;
import com.mammb.code.jpa.fluent.query.Slice;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Pagination.
 * Helper for the view page links.
 * @author Naotsugu Kobayashi
 */
public interface Pagination {

    /**
     * Get the enclosed slice.
     * @return the enclosed slice
     */
    Slice<?> getSlice();


    /**
     * Create the {@link Pagination} for given slice.
     * @param slice the slice
     * @return the {@link Pagination}
     */
    static Pagination of(Slice<?> slice) {
        return () -> slice;
    }


    /**
     * Get the maximum number of links to be displayed.
     * @return the maximum number of links
     */
    default int getMaxLinks() {
        return 9;
    }


    /**
     * Get the number of the current page.
     * @return the number of the current page
     */
    default int getCurrentPage() {
        return getSlice().getNumber();
    }


    /**
     * Gets whether the link to the top page is valid.
     * @return {@code true} if the link to the top page is valid
     */
    default boolean isFirstPageEnable() {
        return getCurrentPage() > 0;
    }


    /**
     * Gets whether the link to the last page is valid.
     * @return {@code true} if the link to the last page is valid
     */
    default boolean isLastPageEnable() {
        return Objects.nonNull(getTotalPages()) && getTotalPages() > getCurrentPage() + 1;
    }


    /**
     * Get the number of total pages.
     * @return the number of total pages
     */
    default Integer getTotalPages() {
        return (getSlice() instanceof Page page) ? page.getTotalPages() : null;
    }


    /**
     * Get the number of starting pages for page links.
     * @return the number of starting pages for page links
     */
    default int startPages() {
        if (Objects.nonNull(getTotalPages())) {
            var start = Math.max(0, getCurrentPage() - getMaxLinks() / 2);
            if (start + getMaxLinks() > getTotalPages() - 1) {
                return Math.max(0, getTotalPages() - getMaxLinks());
            } else {
                return start;
            }
        } else {
            return Math.max(0, getCurrentPage() - getMaxLinks());
        }
    }


    /**
     * Get the number of ending pages for page links.
     * @return the number of ending pages for page links
     */
    default int endPages() {
        return Objects.nonNull(getTotalPages())
            ? Math.min(startPages() + getMaxLinks(), getTotalPages())
            : getCurrentPage() + 1;
    }


    /**
     * Gets whether the link to the previous page is valid.
     * @return {@code true} if the link to the previous page is valid
     */
    default boolean isPervPageEnable() {
        return getSlice().hasPrevious();
    }


    /**
     * Gets whether the link to the next page is valid.
     * @return {@code true} if the link to the next page is valid
     */
    default boolean isNextPageEnable() {
        return getSlice().hasNext();
    }


    /**
     * Get the offset of the beginning of the current page.
     * @return the offset of the beginning of the current page
     */
    default long getElementsTop() {
        return getSlice().getOffset();
    }


    /**
     * Get the offset of the ending of the current page.
     * @return the offset of the ending of the current page
     */
    default long getElementsBottom() {
        return getElementsTop() + getSlice().getContent().size();
    }


    /**
     * Get the total amount of elements.
     * @return the total amount of elements
     */
    default Long getTotalElements() {
        return (getSlice() instanceof Page page) ? page.getTotalElements() : null;
    }


    /**
     * Gets whether the total pages is enabled.
     * @return {@code true} if the total pages is enabled
     */
    default boolean isTotalPagesEnable() {
        return Objects.nonNull(getTotalPages());
    }


    /**
     * Gets whether the total elements is enabled.
     * @return {@code true} if the total elements is enabled
     */
    default boolean isTotalElementsEnable() {
        return Objects.nonNull(getTotalElements());
    }


    /**
     * Get the links list to be displayed.
     * @return the links list to be displayed
     */
    default List<Integer> getLinks() {
        return IntStream.range(startPages(), endPages()).boxed().collect(Collectors.toList());
    }

}
