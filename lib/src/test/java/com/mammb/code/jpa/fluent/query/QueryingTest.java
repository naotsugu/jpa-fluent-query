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
import org.junit.jupiter.api.*;

import java.util.ArrayList;
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
        createIssues();
    }

    @AfterEach
    void tearDown() {
        em.getTransaction().rollback();
    }


    /**
     * <pre>
     * SELECT COUNT(ID) FROM ISSUE
     * </pre>
     */
    @Test
    void testCount() {
        var count = Querying.of(IssueModel.root()).count().on(em);
        assertEquals(6L, count);
    }

    /**
     * <pre>
     * SELECT COUNT(ID) FROM ISSUE WHERE (TITLE = 'foo')
     * </pre>
     */
    @Test
    void testCountWithFilter() {
        var count = Querying.of(IssueModel.root())
            .filter(issue -> issue.getTitle().eq("foo"))
            .count().on(em);
        assertEquals(3L, count);
    }

    /**
     * <pre>
     * SELECT ID, CREATEDON, DESCRIPTION, LASTMODIFIEDON, TITLE, VERSION, PROJECT_ID FROM ISSUE ORDER BY ID ASC
     * </pre>
     */
    @Test
    void testSimpleQuery() {
        List<Issue> issues = Querying.of(IssueModel.root())
            .toList().on(em);
        assertEquals(6, issues.size());
    }


    /**
     * <pre>
     * SELECT ID, CREATEDON, DESCRIPTION, LASTMODIFIEDON, TITLE, VERSION, PROJECT_ID FROM ISSUE WHERE (TITLE = 'foo') ORDER BY ID ASC
     * </pre>
     */
    @Test
    void testSimpleFilter() {
        List<Issue> issues = Querying.of(IssueModel.root())
                .filter(issue -> issue.getTitle().eq("foo"))
                .toList().on(em);
        assertEquals(3, issues.size());
    }

    /**
     * <pre>
     * SELECT t0.* FROM ISSUE t0, PROJECT t1 WHERE (((t1.NAME = ?) AND (t0.TITLE = ?)) AND (t1.ID = t0.PROJECT_ID)) ORDER BY t0.ID ASC
     * </pre>
     */
    @Test
    void testSomeFilter() {
        List<Issue> issues = Querying.of(IssueModel.root())
            .filter(issue -> issue.getProject().getName().eq("name1"))
            .filter(issue -> issue.getTitle().eq("foo"))
            .toList().on(em);
        assertEquals(2, issues.size());
    }

    /**
     * <pre>
     * SELECT t1.* FROM PROJECT t0, ISSUE t1 WHERE ((t1.TITLE = ?) AND (t0.ID = t1.PROJECT_ID))
     * ORDER BY t0.NAME DESC, t1.ID ASC, t1.ID ASC
     * </pre>
     */
    @Test
    void testOrderBy() {
        List<Issue> issues = Querying.of(IssueModel.root())
            .filter(issue -> issue.getTitle().eq("foo"))
            .sorted(issue -> issue.getProject().getName().desc(),
                    issue -> issue.getId().asc())
            .toList().on(em);

        assertEquals("name2", issues.get(0).getProject().getName());
        assertEquals("name1", issues.get(1).getProject().getName());
        assertEquals("name1", issues.get(2).getProject().getName());

    }

    /**
     * <pre>
     * SELECT ID, TITLE FROM ISSUE WHERE (TITLE = ?) ORDER BY ID ASC
     * </pre>
     */
    @Test
    void testMapperConstruct() {
        List<IssueDto> issues = Querying.of(IssueModel.root())
            .filter(r -> r.getTitle().eq("foo"))
            .map(Mappers.issueDto(r -> r.getId(), r -> r.getTitle()))
            .toList().on(em);
        assertEquals("foo", issues.get(0).title());
    }

    /**
     * <pre>
     * SELECT ID AS a1, CREATEDON AS a2, DESCRIPTION AS a3, LASTMODIFIEDON AS a4, TITLE AS a5, VERSION AS a6, PROJECT_ID AS a7
     * FROM ISSUE WHERE (TITLE = 'foo') ORDER BY ID ASC LIMIT 16 OFFSET 0
     * </pre>
     */
    @Test
    void testSlice() {
        Slice<Issue> issues = Querying.of(IssueModel.root())
            .filter(issue -> issue.getTitle().eq("foo"))
            .toSlice(SlicePoint.of()).on(em);

        assertEquals(3, issues.getContent().size());
        assertFalse(issues.hasNext());
    }

    /**
     * <pre>
     * SELECT COUNT(ID) FROM ISSUE WHERE (TITLE = 'foo')
     * SELECT ID AS a1, CREATEDON AS a2, DESCRIPTION AS a3, LASTMODIFIEDON AS a4, TITLE AS a5, VERSION AS a6, PROJECT_ID AS a7
     * FROM ISSUE WHERE (TITLE = 'foo') ORDER BY ID ASC LIMIT 15 OFFSET 0
     * </pre>
     */
    @Test
    void testPage() {
        Page<Issue> issues = Querying.of(IssueModel.root())
            .filter(issue -> issue.getTitle().eq("foo"))
            .toPage(SlicePoint.of()).on(em);
        assertEquals(1, issues.getTotalPages());
        assertEquals(3, issues.getTotalElements());
    }

    /**
     * <pre>
     * SELECT t0.* FROM ISSUE t0 WHERE EXISTS (
     *      SELECT 1 FROM PROJECT t2, PROJECT t1 WHERE (((t1.NAME = 'name1') AND (t1.ID = t2.ID)) AND (t2.ID = t0.PROJECT_ID))
     * ) ORDER BY t0.ID ASC
     * </pre>
     */
    @Test
    void testSubQueryExists() {
        List<Issue> issues = Querying.of(IssueModel.root())
            .filter(issue -> SubQuery.of(ProjectModel.root())
                                        .filter(prj -> prj.getName().eq("name1"))
                                        .filter(prj -> prj.getId().eq(issue.getProject().getId()))
                                        .exists())
            .toList().on(em);
        assertEquals(3, issues.size());
    }


    /**
     * <pre>
     * SELECT t0.* FROM ISSUE t0 WHERE (t0.ID > (
     *      SELECT t1.ID FROM PROJECT t1 WHERE (t1.NAME = ?))
     * ) ORDER BY t0.ID ASC
     * </pre>
     */
    @Test
    void testSubQuery() {
        Querying.of(IssueModel.root())
            .filter(issue -> issue.getId().gt(
                    SubQuery.of(ProjectModel.root())
                               .filter(prj -> prj.getName().eq("name1"))
                               .to(Long.class, prj -> prj.getId())))
            .toList().on(em);
    }


    /**
     * Hibernate is strange.
     * <pre>
     * select i1_0.* from Issue i1_0 where exists(
     *     select i1_0.id where i1_0.title=?
     * ) order by i1_0.id asc
     * </pre>
     *
     * Eclipselink is good.
     * <pre>
     * SELECT t0.* FROM ISSUE t0 WHERE EXISTS (
     *      SELECT 1 FROM ISSUE t1 WHERE ((t1.TITLE = 'foo') AND (t1.ID = t0.ID))
     * ) ORDER BY t0.ID ASC
     * </pre>
     */
    @Test
    void testSelfCorrelate() {
        Querying.of(IssueModel.root())
                .filter(issue -> SubQuery.of(issue)
                                         .filter(r -> r.getTitle().eq("foo"))
                                         .exists())
            .toList().on(em);
    }

    /**
     * Hibernate creates broken SQL.
     * <pre>
     * select i.* from Issue i where exists(
     *   select p.id from Project p where p.name = ? and i.project_id = p.id
     * ) order by i.id asc
     * </pre>
     *
     * Eclipselink is good.
     * <pre>
     * SELECT t0.* FROM ISSUE t0 WHERE EXISTS (
     *      SELECT 1 FROM ISSUE t2, PROJECT t1 WHERE (((t1.NAME = 'name1') AND (t2.PROJECT_ID = t1.ID)) AND (t2.ID = t0.ID))
     * ) ORDER BY t0.ID ASC
     * </pre>
     */
    @Test
    void testCorrelate() {
        Querying.of(IssueModel.root())
            .filter(issue -> SubQuery.of(ProjectModel.root())
                    .filter(prj -> prj.getName().eq("name1"))
                    .filter(issue, (issue1, prj) -> issue1.getProject().eq(prj))
                    .exists())
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
