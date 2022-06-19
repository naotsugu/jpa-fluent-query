package com.mammb.code.jpa.fluent.repository;

import com.mammb.code.jpa.fluent.query.Querying;
import com.mammb.code.jpa.fluent.test.Issue;
import com.mammb.code.jpa.fluent.test.Project;
import com.mammb.code.jpa.fluent.test.Root_;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class RepositoryTest {

    static EntityManagerFactory emf;
    static EntityManager em;
    static IssueRepository repository = new IssueRepository();


    @BeforeAll
    static void initAll() {
        emf = Persistence.createEntityManagerFactory("testUnit");
        em = emf.createEntityManager();
        repository.em = em;
    }


    @AfterAll
    static void tearDownAll() {
        em.close();
        emf.close();
    }


    @Test
    void testRepository() {
        var list = repository.findAll();
        assertEquals(6L, list.size());
    }

}
