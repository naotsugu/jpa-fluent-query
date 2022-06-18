package com.mammb.code.jpa.fluent.repository;

import com.mammb.code.jpa.fluent.test.IssueRepository_;
import jakarta.persistence.EntityManager;

public class IssueRepository implements IssueRepository_ {

    public EntityManager em() {
        return null;
    }

}
