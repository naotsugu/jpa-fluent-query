package com.mammb.code.jpa.fluent.repository;

import com.mammb.code.jpa.fluent.test.Issue;
import com.mammb.code.jpa.fluent.test.IssueRepository_;
import jakarta.persistence.EntityManager;
import java.util.List;

public class IssueRepository implements IssueRepository_ {

    EntityManager em;

    @Override
    public EntityManager em() {
        return em;
    }

    public List<Issue> findByName(String title) {
        return findAll(issue -> issue.getTitle().eq(title),
            sort(issue -> issue.getId().desc()));
    }

    public List<Issue> findByProjectName(String name) {
        return findAll(issue -> issue.getProject().getName().eq("name"),
                       sort(issue -> issue.getId().desc()));
    }

    public List<Issue> findByTitleAndProjectName(String title, String name) {
        return findAll(filter(issue -> issue.getTitle().eq(title))
                .and(issue -> issue.getProject().getName().eq(name)),
            sort(issue -> issue.getTitle().asc()).and(issue -> issue.getId().desc()));
    }

}
