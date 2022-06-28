package com.mammb.code.jpa.fluent.query;

import com.mammb.code.jpa.fluent.test.Issue;
import com.mammb.code.jpa.fluent.test.IssueModel;
import com.mammb.code.jpa.fluent.test.Project;
import com.mammb.code.jpa.fluent.test.ProjectModel;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class QueryingTest {

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
    void testCount() {
        createIssues();
        var count = Querying.of(IssueModel.root()).count().on(em);
        assertEquals(6L, count);
    }


    @Test
    void testCountWithFilter() {
        createIssues();
        var count = Querying.of(IssueModel.root())
            .filter(issue -> issue.getTitle().eq("foo"))
            .count().on(em);
        assertEquals(3L, count);
    }


    @Test
    void testSimpleFilter() {

        createIssues();

        List<Issue> issues = Querying.of(IssueModel.root())
                .toList().on(em);
        assertEquals(6, issues.size());

        issues = Querying.of(IssueModel.root())
                .filter(issue -> issue.getTitle().eq("foo"))
                .toList().on(em);
        assertEquals(3, issues.size());

        issues = Querying.of(IssueModel.root())
                .filter(issue -> issue.getProject().getName().eq("name1"))
                .filter(issue -> issue.getTitle().eq("foo"))
                .toList().on(em);
        assertEquals(2, issues.size());

    }


    @Test
    void testOrderBy() {

        createIssues();

        List<Issue> issues = Querying.of(IssueModel.root())
            .filter(issue -> issue.getTitle().eq("foo"))
            .sorted(issue -> issue.getProject().getName().desc(),
                    issue -> issue.getId().asc())
            .toList().on(em);

        assertEquals("name2", issues.get(0).getProject().getName());
        assertEquals("name1", issues.get(1).getProject().getName());
        assertEquals("name1", issues.get(2).getProject().getName());
    }

    @Test
    void testSlice() {
        createIssues();

        Page<Issue> issues = Querying.of(IssueModel.root())
            .filter(issue -> issue.getTitle().eq("title"))
            .toPage(SlicePoint.of()).on(em);

    }

    @Test
    void testSubQuery() {
        createIssues();
        List<Issue> issues = Querying.of(IssueModel.root())
            .filter(issue -> SubQuery.of(ProjectModel.subRoot())
                                     .filter(prj -> prj.getName().eq("name1"))
                                     .filter(prj -> prj.getId().eq(issue.getProject().getId()))
                                     .exists(issue))
            .toList().on(em);
        assertEquals(3, issues.size());
    }


    private void createIssues() {

        var project1 = new Project(); project1.setName("name1");
        var project2 = new Project(); project2.setName("name2");
        em.persist(project1); em.persist(project2);

        var issue1 = new Issue(); issue1.setTitle("foo"); issue1.setProject(project1);
        var issue2 = new Issue(); issue2.setTitle("foo"); issue2.setProject(project2);
        var issue3 = new Issue(); issue3.setTitle("foo"); issue3.setProject(project1);
        var issue4 = new Issue(); issue4.setTitle("bar"); issue4.setProject(project2);
        var issue5 = new Issue(); issue5.setTitle("bar"); issue5.setProject(project1);
        var issue6 = new Issue(); issue6.setTitle("bar"); issue6.setProject(project2);
        em.persist(issue1); em.persist(issue2); em.persist(issue3);
        em.persist(issue4); em.persist(issue5); em.persist(issue6);
    }

}
