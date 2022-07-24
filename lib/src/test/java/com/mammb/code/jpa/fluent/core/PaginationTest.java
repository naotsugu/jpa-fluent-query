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
import com.mammb.code.jpa.fluent.query.SlicePoint;
import org.junit.jupiter.api.Test;
import java.util.stream.IntStream;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PaginationTest {

    @Test
    void testPaginationFirstPage() {
        var contents = IntStream.rangeClosed(1, 15).mapToObj(String::valueOf).toList();
        var pagination = Pagination.of(Page.of(contents, 62, SlicePoint.of()));
        assertEquals(0, pagination.getCurrentPage());
        assertFalse(pagination.isFirstPageEnable());
        assertTrue(pagination.isLastPageEnable());
        assertEquals(5, pagination.getTotalPages());
        assertEquals(0, pagination.startPages());
        assertEquals(5, pagination.endPages());
        assertFalse(pagination.isPervPageEnable());
        assertTrue(pagination.isNextPageEnable());
        assertEquals(0, pagination.getElementsTop());
        assertEquals(14, pagination.getElementsBottom());
        assertEquals(62, pagination.getTotalElements());
        assertTrue(pagination.isTotalPagesEnable());
        assertTrue(pagination.isTotalElementsEnable());
    }

    @Test
    void testPaginationLastPage() {
        var contents = IntStream.rangeClosed(61, 62).mapToObj(String::valueOf).toList();
        var pagination = Pagination.of(Page.of(contents, 62, SlicePoint.of().withNumber(4)));
        assertEquals(4, pagination.getCurrentPage());
        assertTrue(pagination.isFirstPageEnable());
        assertFalse(pagination.isLastPageEnable());
        assertEquals(5, pagination.getTotalPages());
        assertEquals(0, pagination.startPages());
        assertEquals(5, pagination.endPages());
        assertTrue(pagination.isPervPageEnable());
        assertFalse(pagination.isNextPageEnable());
        assertEquals(60, pagination.getElementsTop());
        assertEquals(61, pagination.getElementsBottom());
        assertEquals(62, pagination.getTotalElements());
        assertTrue(pagination.isTotalPagesEnable());
        assertTrue(pagination.isTotalElementsEnable());
    }

}
