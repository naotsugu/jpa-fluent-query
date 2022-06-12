# JPA Fluent Query


### Filtering

```java
List<Issue> issues = Querying.of(Root_.issue())
        .filter(issue -> issue.getProject().getName().eq("name"))
        .filter(issue -> issue.getTitle().eq("title"))
        .toList().on(em);
```


