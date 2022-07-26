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
import com.mammb.code.jpa.fluent.test.entity.ExternalProject;
import com.mammb.code.jpa.fluent.test.entity.ExternalProjectModel;
import com.mammb.code.jpa.fluent.test.entity.Issue;
import com.mammb.code.jpa.fluent.test.entity.IssueModel;
import com.mammb.code.jpa.fluent.test.entity.Project;
import com.mammb.code.jpa.fluent.test.entity.ProjectModel;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class FilterTest {

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
    void testFilter() {
        var project1 = Fixtures.createProject("project1", em);
        Fixtures.createIssue(project1, "issue1", em);
        Fixtures.createIssue(project1, "issue2", em);
        Fixtures.createIssue(project1, "issue3", em);

        List<Issue> issues = Querying.of(IssueModel.root())
            .filter(issue -> issue.getProject().getName().like("project"))
            .filter(issue -> issue.getTitle().like("issue"))
            .toList().on(em);
        assertEquals(3, issues.size());

        issues = Querying.of(IssueModel.root())
            .filter(issue -> issue.getProject().getName().likePartial("jec"))
            .filter(issue -> issue.getTitle().likePartial("e2"))
            .toList().on(em);
        assertEquals(1, issues.size());

        issues = Querying.of(IssueModel.root())
            .filter(issue -> issue.getProject().getName().eq("project1"))
            .filter(issue -> issue.joinJournals().getPostedOn().gt(LocalDateTime.of(1999, 1, 1, 1, 0)))
            .toList().on(em);
        assertEquals(3, issues.size());

    }


    @Test
    void testFilterJoin() {
        Fixtures.createProject("project1", em);
        List<Project> project = Querying.of(ProjectModel.root())
            .filter(prj -> prj.joinComments().getCommentedBy().like("commentedBy"))
            .toList().on(em);
        assertEquals(1, project.size());

        List<ExternalProject> ext = Querying.of(ExternalProjectModel.root())
            .filter(prj -> prj.joinComments().getCommentedBy().like("commentedBy"))
            .toList().on(em);
        assertEquals(0, ext.size());
    }


    @Test
    void testFilterMapJoin() {
        Fixtures.createProject("project1", em);
        List<Project> project = Querying.of(ProjectModel.root())
            .filter(issue -> issue.joinTasks((k, v) -> k.like("task")))
            .toList().on(em);
        assertEquals(1, project.size());
    }

}
