# JPA Fluent Query

[![Build](https://github.com/naotsugu/jpa-fluent-query/actions/workflows/gradle-build.yml/badge.svg)](https://github.com/naotsugu/jpa-fluent-query/actions/workflows/gradle-build.yml)


## What is this

Query library for Jakarta Persistence.

Generate a fluent API from a static metamodel class using an annotation processor.

**For more information, get familiar with the [JPA Fluent Query User Manual](https://naotsugu.github.io/jpa-fluent-query/).**


## Usage

If you use Gradle Kotlin DSL, define annotation processors and `jpa-fluent-query` dependencies as follows :

```kotlin
dependencies {
  annotationProcessor("org.hibernate.orm:hibernate-jpamodelgen:6.1.0.Final")
  annotationProcessor("com.mammb:jpa-fluent-modelgen:0.6.0")
  implementation("com.mammb:jpa-fluent-query:0.6.0")
}
```

Use `jpa-fluent-modelgen` to generate APIs for queries from static metamodel classes.

`jpa-fluent-query` contains a fluent API for query construction.


## Building Queries

If you have an Issue entity like the following...

```java
@Entity
public class Issue extends BaseEntity {
    @ManyToOne
    private Project project;
    private String title;
}
```

You can get the issue list as follows

```java
List<Issue> issues = Querying.of(IssueModel.root())
    .filter(issue -> issue.getProject().getName().eq("name"))
    .toList().on(em);
```


## Using Repository

For entities, the base class of the repository is automatically generated by the annotation processor.

A repository can be created as follows

```java
public class IssueRepository implements IssueRepository_ {

    @Inject
    private EntityManager em;

    @Override
    public EntityManager em() {
        return em;
    }
}
```

Using the predefined methods, you can retrieve the listings as follows

```java
var list = repository.findAll(issue -> issue.getProject().getName().eq("name"));
```

## Using typesafe mapping

If you use constructor expressions to map query results to DTOs, mapping errors can only be detected at run-time.

This library automatically creates a type-safe method for mapping by preparing a DTO annotated with `@Mappable` as follows.

```java
@Mappable
public record IssueDto(Long id, String title) { }
```

Mapping to DTOs is done as follows.

```java
List<IssueDto> issues = Querying.of(IssueModel.root())
    .map(Mappers.issueDto(r -> r.getId(), r -> r.getTitle()))
    .toList().on(em);
```

The type and number of arguments can be detected at build time.

