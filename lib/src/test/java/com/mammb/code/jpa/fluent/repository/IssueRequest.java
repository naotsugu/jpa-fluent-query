package com.mammb.code.jpa.fluent.repository;

import com.mammb.code.jpa.fluent.query.Filter;
import com.mammb.code.jpa.fluent.query.Request;
import com.mammb.code.jpa.fluent.query.SlicePoint;
import com.mammb.code.jpa.fluent.query.Sorts;
import com.mammb.code.jpa.fluent.test.Issue;
import com.mammb.code.jpa.fluent.test.IssueRoot_;

public class IssueRequest implements Request<Issue, IssueRoot_<Issue>> {

    public String titleLike = "";
    public String description = "";
    private SlicePoint point = SlicePoint.of();

    @Override
    public Filter<Issue, IssueRoot_<Issue>> getFilter() {
        return Filter.of(
            issue -> issue.getTitle().like(titleLike),
            issue -> issue.getDescription().eq(description));
    }

    @Override
    public Sorts<Issue, IssueRoot_<Issue>> getSorts() {
        return Sorts.of(
            issue -> issue.getTitle().asc(),
            issue -> issue.getDescription().desc(),
            issue -> issue.getId().asc());
    }

    @Override
    public SlicePoint getSlicePoint() {
        return point;
    }

}
