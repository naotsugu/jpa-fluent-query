package com.mammb.code.jpa.fluent.query;

import com.mammb.code.jpa.fluent.test.Issue;
import com.mammb.code.jpa.fluent.test.Issue_Root_;
import com.mammb.code.jpa.fluent.test.Project;
import com.mammb.code.jpa.fluent.test.Root_;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
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

        em.getTransaction().begin();

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

        em.getTransaction().commit();

    }


    @AfterAll
    static void tearDownAll() {
        em.close();
        emf.close();
    }


    @Test
    void testCount() {
        var count = Querying.of(Root_.issue()).count().on(em);
        assertEquals(6L, count);
    }


    @Test
    void testCountWithFilter() {
        var count = Querying.of(Root_.issue())
            .filter(issue -> issue.getTitle().eq("foo"))
            .count().on(em);
        assertEquals(3L, count);
    }


    @Test
    void testSimpleFilter() {

        List<Issue> issues = Querying.of(Root_.issue())
                .toList().on(em);
        assertEquals(6, issues.size());

        issues = Querying.of(Root_.issue())
                .filter(issue -> issue.getTitle().eq("foo"))
                .toList().on(em);
        assertEquals(3, issues.size());

        issues = Querying.of(Root_.issue())
                .filter(issue -> issue.getProject().getName().eq("name1"))
                .filter(issue -> issue.getTitle().eq("foo"))
                .toList().on(em);
        assertEquals(2, issues.size());

    }


    @Test
    void testOrderBy() {
        List<Issue> issues = Querying.of(Root_.issue())
            .filter(issue -> issue.getTitle().eq("foo"))
            .sorted(issue -> issue.getProject().getName().desc())
            .sorted(issue -> issue.getId().asc())
            .toList().on(em);
        assertEquals("name2", issues.get(0).getProject().getName());
        assertEquals("name1", issues.get(1).getProject().getName());
        assertEquals("name1", issues.get(2).getProject().getName());
    }

}
