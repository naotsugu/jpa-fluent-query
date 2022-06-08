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

import java.util.List;
import java.util.Objects;

/**
 * Abstract interface for page.
 *
 * @param <T> the type of page element
 * @author Naotsugu Kobayashi
 */
public interface Page<T> extends Slice<T> {

    /**
     * Get the total amount of elements.
     * @return the total amount of elements
     */
    long getTotalElements();


    /**
     * Get the number of total pages.
     * @return the number of total pages
     */
    default int getTotalPages() {
        return getSize() == 0 ? 1 : (int) Math.ceil((double) getTotalElements() / (double) getSize());
    }


    /**
     * Get whether the next {@link Page} exists.
     * @return if there is a next {@link Page}, then {@code true}
     */
    @Override
    default boolean hasNext() {
        return getNumber() + 1 < getTotalPages();
    }


    /**
     * Create the {@link Page} by given arguments.
     * @param content the content of {@link Page}.
     * @param totalElements the total amount of elements
     * @param slicePoint the current point of slice
     * @param <T> the type of content
     * @return the created {@link Page}
     */
    static <T> Page<T> of(List<T> content, long totalElements, SlicePoint slicePoint) {

        return new Page<>() {

            private final List<T> list = Objects.isNull(content) ? List.of() : List.copyOf(content);

            @Override
            public long getTotalElements() {
                return totalElements;
            }

            @Override
            public List<T> getContent() {
                return list;
            }

            @Override
            public int getNumber() {
                return slicePoint.getNumber();
            }

            @Override
            public int getSize() {
                return slicePoint.getSize();
            }

        };
    }

}
