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

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class SlicePointTest {

    @Test void of() {
        SlicePoint slicePoint = SlicePoint.of(0, 10);
        assertEquals(0, slicePoint.getNumber());
        assertEquals(0, slicePoint.getOffset());
        assertEquals(10, slicePoint.getSize());
        assertEquals(1, slicePoint.withNumber(1).getNumber());
        assertEquals(15, slicePoint.withSize(15).getSize());
        assertEquals(1, slicePoint.next().getNumber());
    }

}
