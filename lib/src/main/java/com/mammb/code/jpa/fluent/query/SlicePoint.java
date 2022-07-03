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

/**
 * The current point of slice.
 * @author Naotsugu Kobayashi
 */
public interface SlicePoint {

    /**
     * Get the number of the current {@link SlicePoint}.
     * This number is zero-origin.
     * @return the number of the current {@link SlicePoint}.
     */
    int getNumber();


    /**
     * Get the size of slice.
     * @return the size of slice.
     */
    int getSize();


    /**
     * Get the offset index from the first element.
     * @return the offset index from the first element
     */
    default long getOffset() {
        return (long) getNumber() * (long) getSize();
    }


    /**
     * Create the next {@link SlicePoint}.
     * @return the next {@link SlicePoint}
     */
    default SlicePoint next() {
        return of(getNumber() + 1, getSize());
    }


    /**
     * Create the {@link SlicePoint} with given number.
     * @param number the number of the current {@link SlicePoint}
     * @return the {@link SlicePoint} with given number
     */
    default SlicePoint withNumber(int number) {
        return of(number, getSize());
    }


    /**
     * Create the {@link SlicePoint} with given size.
     * @param size the size of slice
     * @return the {@link SlicePoint} with given size
     */
    default SlicePoint withSize(int size) {
        return of(getNumber(), size);
    }


    /**
     * Create a {@link SlicePoint}.
     * @return a {@link SlicePoint}
     */
    static SlicePoint of() {
        return of(0, 15);
    }


    /**
     * Create a {@link SlicePoint}.
     * @param number the number of the current {@link SlicePoint}
     * @param size the size of slice
     * @return a {@link SlicePoint}
     */
    static SlicePoint of(int number, int size) {
        return new SlicePoint() {
            @Override
            public int getNumber() { return number; }
            @Override
            public int getSize() { return size; }
        };
    }

}
