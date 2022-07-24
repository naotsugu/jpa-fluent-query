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
package com.mammb.code.jpa.fluent.test;

import com.mammb.code.jpa.fluent.test.entity.Comment;
import com.mammb.code.jpa.fluent.test.entity.Duration;
import com.mammb.code.jpa.fluent.test.entity.Issue;
import com.mammb.code.jpa.fluent.test.entity.Journal;
import com.mammb.code.jpa.fluent.test.entity.Project;
import com.mammb.code.jpa.fluent.test.entity.ProjectState;
import com.mammb.code.jpa.fluent.test.entity.Tag;
import jakarta.persistence.EntityManager;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class Fixtures {

    static final Set<Tag> tags = createTags();

    public static Project createProject(String name, EntityManager em) {
        var project = createProject(name);
        em.persist(project);
        return project;
    }

    public static Issue createIssue(Project project, String title, EntityManager em) {
        var issue = createIssue(project, title);
        issue.getJournals().stream().map(Journal::getComments).flatMap(Collection::stream).forEach(em::persist);
        issue.getJournals().forEach(em::persist);
        issue.getTags().forEach(tag -> { if (Objects.isNull(tag.getId())) em.persist(tag); });
        em.persist(issue);
        return issue;
    }

    private static Project createProject(String name) {
        var project = new Project();
        var duration = new Duration();
        project.setName(name);
        project.setState(ProjectState.OPEN);
        project.setDuration(duration);
        duration.setOpen(LocalDate.of(2000, 1, 1));
        duration.setClose(LocalDate.of(2001, 1, 1));
        return project;
    }

    private static Project addChild(Project parent) {
        var child = createProject(parent.getName() + "-child");
        child.setState(parent.getState());
        child.setDuration(parent.getDuration());
        child.setParent(parent);
        return child;
    }

    private static Issue createIssue(Project project, String title) {
        var issue = new Issue();
        issue.setTitle(title);
        issue.setPriority(1);
        issue.setDescription(title);
        issue.setProject(project);
        issue.setJournals(createJournals());
        issue.setTags(tags);
        return issue;
    }

    private static List<Journal> createJournals() {
        return Arrays.asList(createJournal("content1"), createJournal("content2"));
    }

    private static Journal createJournal(String content) {
        var journal = new Journal();
        journal.setPostedOn(LocalDateTime.of(LocalDate.of(2000, 1, 1), LocalTime.MIDNIGHT));
        journal.setPostedBy("postedBy");
        journal.setContent(content);
        journal.setComments(createComments());
        return journal;
    }

    private static Set<Tag> createTags() {
        var tag1 = new Tag();
        tag1.setName("tag1");
        var tag2 = new Tag();
        tag2.setName("tag2");
        return new HashSet<>(Arrays.asList(tag1, tag2));
    }

    private static List<Comment> createComments() {

        var comment1 = new Comment();
        comment1.setCommentedBy("commentedBy1");
        comment1.setCommentedOn(LocalDateTime.of(LocalDate.of(2000, 1, 1), LocalTime.MIDNIGHT));
        comment1.setContent("content1");

        var comment2 = new Comment();
        comment2.setCommentedBy("commentedBy1");
        comment2.setCommentedOn(LocalDateTime.of(LocalDate.of(2000, 1, 1), LocalTime.MIDNIGHT));
        comment2.setContent("content1");

        return Arrays.asList(comment1, comment2);
    }

}
