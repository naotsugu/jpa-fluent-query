package com.mammb.code.jpa.fluent.query;

import com.mammb.code.jpa.fluent.test.Fixtures;
import com.mammb.code.jpa.fluent.test.entity.Issue;
import com.mammb.code.jpa.fluent.test.entity.IssueModel;
import com.mammb.code.jpa.fluent.test.entity.Project;
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
        var issue1 = Fixtures.createIssue(project1, "issue1", em);
        var issue2 = Fixtures.createIssue(project1, "issue2", em);
        var issue3 = Fixtures.createIssue(project1, "issue3", em);

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


}
