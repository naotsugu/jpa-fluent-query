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

import com.mammb.code.jpa.fluent.test.Fixtures;
import com.mammb.code.jpa.fluent.test.entity.IssueModel;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

class SliceStreamTest {

    static EntityManagerFactory emf;
    static EntityManager em;

    @BeforeAll
    static void initAll() {
        emf = Persistence.createEntityManagerFactory("testUnit");
        em = emf.createEntityManager();
    }

    @AfterAll
    static void tearDownAll() { em.close(); emf.close(); }

    @BeforeEach
    void init() {
        em.getTransaction().begin();
    }

    @AfterEach
    void tearDown() {
        em.getTransaction().rollback();
    }


    @Test
    void testStream() {

        var project1 = Fixtures.createProject("StreamingSliceTest", em);
        IntStream.range(1, 20).forEach(i -> Fixtures.createIssue(project1, "issue" + i, em));

        var issues = Querying.of(IssueModel.root())
            .filter(issue -> issue.getProject().getName().eq("StreamingSliceTest"))
            .toStream(5).on(em).toList();

        assertEquals("issue19", issues.get(0).getTitle());
        assertEquals("issue1", issues.get(18).getTitle());
        assertEquals(19, issues.size());

    }


    @Test
    void testForwardingStream() {

        var project1 = Fixtures.createProject("StreamingSliceTest", em);
        IntStream.rangeClosed(1, 20).forEach(i -> Fixtures.createIssue(project1, "issue" + i, em));

        var issues = Querying.of(IssueModel.root())
            .filter(issue -> issue.getProject().getName().eq("StreamingSliceTest"))
            .toForwardingStream(5).on(em).toList();

        assertEquals("issue1", issues.get(0).getTitle());
        assertEquals("issue20", issues.get(19).getTitle());
        assertEquals(20, issues.size());

    }

}
