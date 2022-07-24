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
import com.mammb.code.jpa.fluent.test.entity.IssueRepository_;
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
