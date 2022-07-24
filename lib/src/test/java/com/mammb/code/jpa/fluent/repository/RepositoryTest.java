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
package com.mammb.code.jpa.fluent.repository;

import com.mammb.code.jpa.fluent.test.entity.Issue;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
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

    @BeforeEach
    void init() {
        em.getTransaction().begin();
    }

    @AfterEach
    void tearDown() {
        em.getTransaction().rollback();
    }


    @Test
    void testRepository() {

        Issue issue = new Issue();
        issue.setTitle("issueTitle");
        issue.setDescription("description");
        repository.saveAndFlash(issue);

        var list = repository.findAll(r -> r.getTitle().eq("issueTitle"));
        assertEquals(1L, list.size());

        list = repository.findAll(r -> r.getProject().getName().eq("ma,e"));
    }


    @Test
    void testRequestRepository() {
        Issue issue1 = new Issue();
        issue1.setTitle("testRequestRepository1");
        Issue issue2 = new Issue();
        issue2.setTitle("testRequestRepository2");
        repository.save(issue1);
        repository.save(issue2);

        IssueRequest req = new IssueRequest();
        req.titleLike = "testRequestRepository";

        var list = repository.findAll(req);
        assertEquals(2L, list.size());
    }

}
