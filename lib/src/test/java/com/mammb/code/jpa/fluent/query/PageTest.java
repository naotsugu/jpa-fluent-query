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
import java.util.stream.IntStream;
import static org.junit.jupiter.api.Assertions.*;

class PageTest {

    @Test
    void of() {
        Page<String> page = Page.of(
            IntStream.rangeClosed(1, 10).mapToObj(String::valueOf).toList(),
            15,
            SlicePoint.of(0, 10));
        assertEquals(10, page.getSize());
        assertEquals(0, page.getNumber());
        assertEquals(10, page.getContent().size());
        assertEquals(15, page.getTotalElements());
        assertEquals(2, page.getTotalPages());
        assertTrue(page.hasContent());
        assertFalse(page.hasPrevious());
    }

}
