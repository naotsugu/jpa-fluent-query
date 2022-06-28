# JPA Fluent Query

This project is under development.


## What is this

Jakarta Persistence Query Utility.


## Usage

If you use Gradle Kotlin DSL, define annotation processors and `jpa-fluent-query` dependencies as follows :

```kotlin
dependencies {
  annotationProcessor("org.hibernate.orm:hibernate-jpamodelgen:6.1.0.Final")
  annotationProcessor("com.mammb:jpa-fluent-modelgen:0.5.0")
  testImplementation("com.mammb:jpa-fluent-query:0.5.0")
}
```

## Example

### Filter(Where)

```java
List<Issue> issues = Querying.of(IssueModel.root())
    .filter(issue -> issue.getProject().getName().eq("name"))
    .filter(issue -> issue.getTitle().eq("foo"))
    .toList().on(em);
```


### Count

```java
var count = Querying.of(IssueModel.root())
    .filter(issue -> issue.getTitle().eq("foo"))
    .count().on(em);
```


### Sort(OrderBy)

```java
List<Issue> issues = Querying.of(IssueModel.root())
    .filter(issue -> issue.getTitle().eq("name"))
    .sorted(issue -> issue.getProject().getName().desc(),
            issue -> issue.getId().asc())
    .toList().on(em);
```


### Slice

```java
Slice<Issue> issues = Querying.of(IssueModel.root())
    .filter(issue -> issue.getTitle().eq("title"))
    .toSlice(SlicePoint.of()).on(em);
```


### Page

```java
Page<Issue> issues = Querying.of(IssueModel.root())
    .filter(issue -> issue.getTitle().eq("title"))
    .toPage(SlicePoint.of()).on(em);
```


### SubQuery

```java
List<Issue> issues = Querying.of(IssueModel.root())
    .filter(issue -> SubQuery.of(ProjectModel.subRoot())
                             .filter(prj -> prj.getName().eq("name"))
                             .filter(prj -> prj.getId().eq(issue.getProject().getId()))
                             .exists(issue))
    .toList().on(em);
```

