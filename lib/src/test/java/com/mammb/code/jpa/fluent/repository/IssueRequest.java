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

import com.mammb.code.jpa.fluent.query.Filter;
import com.mammb.code.jpa.fluent.query.Request;
import com.mammb.code.jpa.fluent.query.SlicePoint;
import com.mammb.code.jpa.fluent.query.Sorts;
import com.mammb.code.jpa.fluent.test.entity.Issue;
import com.mammb.code.jpa.fluent.test.entity.IssueModel.*;

public class IssueRequest implements Request<Issue, Root_> {

    public String titleLike = "";
    public String description = "";
    private SlicePoint point = SlicePoint.of();

    @Override
    public Filter<Issue, Root_> getFilter() {
        return Filter.of(
            issue -> issue.getTitle().like(titleLike),
            issue -> issue.getDescription().eq(description));
    }

    @Override
    public Sorts<Issue, Root_> getSorts() {
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
