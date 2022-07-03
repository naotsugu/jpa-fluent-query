package com.mammb.code.jpa.fluent.test;

import com.mammb.code.jpa.core.Criteria;
import com.mammb.code.jpa.fluent.query.Mapper;
import com.mammb.code.jpa.core.RootAware;
import com.mammb.code.jpa.fluent.query.Selector;
import java.util.Arrays;

public abstract class Mappers {

    public static <E, R extends RootAware<E>> Mapper<E, R, IssueDto> issueDto(
            Criteria.Selector<E, R, Long> e1, Criteria.Selector<E, R, String> e2) {
        return Mapper.construct(IssueDto.class, Arrays.asList(Selector.of(e1), Selector.of(e2)));
    }

}
