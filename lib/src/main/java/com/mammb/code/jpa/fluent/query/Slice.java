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
 * A slice of data elements.
 * @param <E> the type of entry
 * @author Naotsugu Kobayashi
 */
public interface Slice<E> extends SlicePoint {

    /**
     * Get the content of {@link Slice} as {@link List}.
     * @return the content of {@link Slice}
     */
    List<E> getContent();


    /**
     * Gets whether the {@link Slice} has content.
     * @return if the {@link Slice} has content, then {@code true}
     */
    default boolean hasContent() {
        return Objects.nonNull(getContent()) && getContent().size() > 0;
    }


    /**
     * Get whether the next {@link Slice} exists.
     * @return if there is a next {@link Slice}, then {@code true}
     */
    boolean hasNext();


    /**
     * Get whether the previous {@link Slice} exists.
     * @return if there is a previous {@link Slice}, then {@code true}
     */
    default boolean hasPrevious() {
        return getNumber() > 0;
    }


    /**
     * Create the {@link Slice} by given arguments.
     * @param content the content of {@link Slice}.
     * @param hasNext whether the next slice exists
     * @param slicePoint the current point of slice
     * @param <E> the type of content
     * @return the created {@link Slice}
     */
    static <E> Slice<E> of(List<E> content, boolean hasNext, SlicePoint slicePoint) {

        return new Slice<>() {

            private final List<E> list = Objects.isNull(content) ? List.of() : List.copyOf(content);

            @Override
            public int getNumber() {
                return slicePoint.getNumber();
            }

            @Override
            public int getSize() {
                return slicePoint.getSize();
            }

            @Override
            public List<E> getContent() {
                return list;
            }

            @Override
            public boolean hasNext() {
                return hasNext;
            }
        };
    }

}
