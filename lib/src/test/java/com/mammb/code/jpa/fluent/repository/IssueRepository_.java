package com.mammb.code.jpa.fluent.repository;

import com.mammb.code.jpa.core.RootSource;
import com.mammb.code.jpa.fluent.test.Issue;
import com.mammb.code.jpa.fluent.test.Issue_Root_;
import com.mammb.code.jpa.fluent.test.Root_;

public interface IssueRepository_ extends BaseRepository<Long, Issue, Issue_Root_<Issue>> {
    default RootSource<Issue, Issue_Root_<Issue>> rootSource() {
        return Root_.issue();
    }
}
