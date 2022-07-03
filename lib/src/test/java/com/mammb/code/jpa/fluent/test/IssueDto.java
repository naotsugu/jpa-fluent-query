package com.mammb.code.jpa.fluent.test;

import com.mammb.code.jpa.core.Mappable;

@Mappable
public record IssueDto(Long id, String title) { }
