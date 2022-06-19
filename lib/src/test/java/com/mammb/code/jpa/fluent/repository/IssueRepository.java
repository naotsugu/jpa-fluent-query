package com.mammb.code.jpa.fluent.repository;

import com.mammb.code.jpa.fluent.test.Issue;
import com.mammb.code.jpa.fluent.test.IssueRepository_;
import jakarta.persistence.EntityManager;

import java.util.List;

public class IssueRepository implements IssueRepository_ {

    EntityManager em;
    public EntityManager em() {
        return em;
    }

    public List<Issue> findByName(String name) {
        return findAll(filter().and(issue -> issue.getTitle().eq(name)),
                       sort().and(issue -> issue.getId().desc()));
    }

}
