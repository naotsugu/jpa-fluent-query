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
