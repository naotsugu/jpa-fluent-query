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
     * select count(i.id) from Issue i
     * </pre>
     */
    @Test
    void testCount() {
        var count = Querying.of(IssueModel.root()).count().on(em);
        assertEquals(6L, count);
    }

    /**
     * <pre>
     * select count(i.id) from Issue i where i.title = ?
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
     * select i.* from Issue i order by i.id asc
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
     * select * from Issue i where i.title = ? order by i.id asc
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
     * select i.* from Issue i join Project p on p.id = i.project_id
     * where p.name = ? and i.title = ? order by i.id asc
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
     * select i.* from Issue i join Project p on p.id = i.project_id where i.title= ?
     * order by p.name desc, i.id asc, i.id asc
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
     * select i.id, i.title from Issue i where i.title = ? order by i.id asc
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
     * select * from Issue i where i.title = ? order by i.id asc
     * offset ? rows fetch first ? rows only
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
     * select count(i.id) from Issue i where i.title = ?
     * </pre>
     *
     * <pre>
     * select * from Issue i where i.title = ? order by i.id asc
     * offset ? rows fetch first ? rows only
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
     * select
     *             i1_0.id,
     *             i1_0.createdOn,
     *             i1_0.description,
     *             i1_0.lastModifiedOn,
     *             i1_0.project_id,
     *             i1_0.title,
     *             i1_0.version
     *         from
     *             Issue i1_0
     *         where
     *             exists(select
     *                 p1_0.id
     *             from
     *                 Project p1_0
     *             where
     *                 p1_0.name=?
     *                 and p1_0.id=i1_0.project_id)
     *         order by
     *             i1_0.id asc
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
     *         select
     *             i1_0.id,
     *             i1_0.createdOn,
     *             i1_0.description,
     *             i1_0.lastModifiedOn,
     *             i1_0.project_id,
     *             i1_0.title,
     *             i1_0.version
     *         from
     *             Issue i1_0
     *         where
     *             i1_0.id>(
     *                 select
     *                     p1_0.id
     *                 from
     *                     Project p1_0
     *                 where
     *                     p1_0.name=?
     *             )
     *         order by
     *             i1_0.id asc
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
     * <pre>
     * select i1.* from Issue i1 where exists(
     *   select i2.id from Issue i2 where i2.title = ? and i2.id = i1.id
     * ) order by i1.id asc
     * </pre>
     */
    @Test
    void testSelfCorrelate() {
        Querying.of(IssueModel.root())
                .filter(issue -> SubQuery.of(issue)
                                            .filter(r -> r.getTitle().eq("foo"))
                                            .filter(r -> r.getId().eq(issue.getId()))
                                            .exists())
            .toList().on(em);
    }

    /**
     * <pre>
     * select i.* from Issue i where exists(
     *   select p.id from Project p where p.name = ? and i.project_id = p.id
     * ) order by i.id asc
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
