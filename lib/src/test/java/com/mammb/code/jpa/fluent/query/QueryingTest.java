package com.mammb.code.jpa.fluent.query;

import com.mammb.code.jpa.fluent.test.Issue;
import com.mammb.code.jpa.fluent.test.IssueDto;
import com.mammb.code.jpa.fluent.test.IssueModel;
import com.mammb.code.jpa.fluent.test.Mappers;
import com.mammb.code.jpa.fluent.test.Project;
import com.mammb.code.jpa.fluent.test.ProjectModel;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.persistence.criteria.Root;
import org.junit.jupiter.api.*;
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
    void testSimpleQuery() {
        createIssues();
        List<Issue> issues = Querying.of(IssueModel.root())
            .toList().on(em);
        assertEquals(6, issues.size());
    }


    @Test
    void testSimpleFilter() {
        createIssues();
        List<Issue> issues = Querying.of(IssueModel.root())
                .filter(issue -> issue.getTitle().eq("foo"))
                .toList().on(em);
        assertEquals(3, issues.size());
    }


    @Test
    void testSomeFilter() {
        createIssues();
        List<Issue> issues = Querying.of(IssueModel.root())
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
    void testMapperConstruct() {
        createIssues();
        List<IssueDto> issues = Querying.of(IssueModel.root())
            .filter(r -> r.getTitle().eq("foo"))
            .map(Mappers.issueDto(r -> r.getId(), r -> r.getTitle()))
            .toList().on(em);
        assertEquals("foo", issues.get(0).title());
    }

    @Test
    void testSlice() {
        createIssues();
        Slice<Issue> issues = Querying.of(IssueModel.root())
            .filter(issue -> issue.getTitle().eq("foo"))
            .toSlice(SlicePoint.of()).on(em);
        assertEquals(3, issues.getContent().size());
        assertFalse(issues.hasNext());
    }

    @Test
    void testPage() {
        createIssues();
        Page<Issue> issues = Querying.of(IssueModel.root())
            .filter(issue -> issue.getTitle().eq("foo"))
            .toPage(SlicePoint.of()).on(em);
        assertEquals(1, issues.getTotalPages());
        assertEquals(3, issues.getTotalElements());
    }

    @Test
    void testSubQueryExists() {
        createIssues();
        List<Issue> issues = Querying.of(IssueModel.root())
            .filter(issue -> SubQuerying.of(ProjectModel.root())
                                        .filter(prj -> prj.getName().eq("name1"))
                                        .filter(prj -> prj.getId().eq(issue.getProject().getId()))
                                        .exists())
            .toList().on(em);
        assertEquals(3, issues.size());
    }


    @Test
    void testSubQuery() {
        createIssues();
        Querying.of(IssueModel.root())
            .filter(issue -> issue.getId().gt(
                SubQuerying.of(ProjectModel.root())
                          .filter(prj -> prj.getName().eq("name1"))
                          .toExpression(Long.class, prj -> prj.getId())))
            .toList().on(em);
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
