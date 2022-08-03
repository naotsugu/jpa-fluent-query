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
import java.util.Objects;

/**
 * The query slice request.
 * @param <E> the type of entity
 * @param <R> the type of root
 * @author Naotsugu Kobayashi
 */
public interface SliceRequest<E, R extends RootAware<E>> extends Request<E, R>, SlicePoint {

    /**
     * Get the {@link SlicePoint}.
     * @return the {@link SlicePoint}
     */
    default SlicePoint getSlicePoint() {
        return SlicePoint.of();
    }


    @Override
    default int getNumber() {
        return getSlicePoint().getNumber();
    }


    @Override
    default int getSize() {
        return getSlicePoint().getSize();
    }


    @Override
    default SliceRequest<E, R> next() {
        return of(getFilter(), getSorts(), getSlicePoint().next());
    }


    /**
     * Create the {@link SliceRequest} with given slice point.
     * @param point a {@link SlicePoint}
     * @return the {@link SliceRequest}
     */
    default SliceRequest<E, R> withPoint(SlicePoint point) {
        return of(getFilter(), getSorts(), point);
    }


    @Override
    default SliceRequest<E, R> withNumber(int number) {
        return of(getFilter(), getSorts(), getSlicePoint().withNumber(number));
    }


    @Override
    default SliceRequest<E, R> withSize(int size) {
        return of(getFilter(), getSorts(), getSlicePoint().withSize(size));
    }


    /**
     * Create a {@link SliceRequest}
     * @param filter a {@link Filter}
     * @param sorts a {@link Sorts}
     * @param slicePoint a {@link SlicePoint}
     * @param <E> the type of entity
     * @param <R> the type of root
     * @return the {@link SliceRequest}
     */
    static <E, R extends RootAware<E>> SliceRequest<E, R> of(
            Filter<E, R> filter, Sorts<E, R> sorts, SlicePoint slicePoint) {
        return new SliceRequest<>() {
            @Override
            public Filter<E, R> getFilter() { return Objects.isNull(filter) ? Filter.empty() : filter; }
            @Override
            public Sorts<E, R> getSorts() { return Objects.isNull(sorts) ? Sorts.empty() : sorts; }
            @Override
            public SlicePoint getSlicePoint() { return Objects.isNull(slicePoint) ? SlicePoint.of() : slicePoint; }
        };
    }

}
