Mirage-SQL [![Maven Central](https://img.shields.io/maven-central/v/vn.com.unit.miragesql/miragesql)](https://mvnrepository.com/artifact/vn.com.unit.miragesql/miragesql)
======

> **Lifecycle policy:** `5.x` is the Current standard artifact line for active development. Supported `4.x` lines are maintenance-only.
>
> A lifecycle designation does not prove that a release is available. Confirm version status and runtime requirements in [Compatibility](COMPATIBILITY.md).

The Legacy artifact family is a separate compatibility distribution. It uses artifactIds with the `-legacy` suffix and retains the `com.miragesql` Java package. Its source is maintained on the [`maintenance/legacy`](https://github.com/unit-corp/mirage/tree/maintenance/legacy) branch. Legacy artifacts and standard artifacts have separate maintenance branches and release lifecycles.

**`Mirage-SQL`** is an easy and powerful SQL-centric database access library for Java (or JVM based languages) which provides dynamic SQL templates in plain SQL files.

This project is a fork of [Mirage-SQL](https://github.com/mirage-sql/mirage) maintained by UNIT Technology Corporation since 2025.

Select a supported Mirage-SQL release based on your Java, Spring, and database baseline. See [Compatibility](COMPATIBILITY.md) for the version matrix.

### Project structure
This is a multi-module Maven build. The root `miragesql-parent` is the parent POM (`pom` packaging). It holds the shared configuration and the managed versions of the modules below. You do not declare it in your application. Maven resolves it automatically when it resolves a module.

Module|Artifact|Description
---   |---     |---
**Mirage-SQL**|`miragesql`|The core library.
**Mirage-SQL Test**|`miragesql-test`|The testing functionality.
**Mirage-SQL Integration**|`miragesql-integration`|The integration with [Spring](https://projects.spring.io/spring-framework/), [Guice](https://github.com/google/guice) and [Seasar2](http://www.seasar.org/en/).

All modules in a release use the same version. The upstream **Mirage-SQL Tools** module (`miragesql-tools`) is not part of this repository.

### Using
Confirm the required version is available in your artifact repository before adopting it. Define the selected version once and use it for all modules.

```xml
<properties>
    <miragesql.version>REPLACE_WITH_VERSION</miragesql.version>
</properties>
```

```xml
<dependency>
    <groupId>vn.com.unit.miragesql</groupId>
    <artifactId>miragesql</artifactId>
    <version>${miragesql.version}</version>
</dependency>

<!-- optional: Spring, Guice or Seasar2 integration -->
<dependency>
    <groupId>vn.com.unit.miragesql</groupId>
    <artifactId>miragesql-integration</artifactId>
    <version>${miragesql.version}</version>
</dependency>

<!-- optional: testing functionality -->
<dependency>
    <groupId>vn.com.unit.miragesql</groupId>
    <artifactId>miragesql-test</artifactId>
    <version>${miragesql.version}</version>
    <scope>test</scope>
</dependency>
```
To declare the version once, import the parent POM in the `dependencyManagement` of your `pom.xml`, then omit the version on each module:

```xml
<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>vn.com.unit.miragesql</groupId>
            <artifactId>miragesql-parent</artifactId>
            <version>${miragesql.version}</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
    </dependencies>
</dependencyManagement>

<dependencies>
    <dependency>
        <groupId>vn.com.unit.miragesql</groupId>
        <artifactId>miragesql</artifactId>
    </dependency>
</dependencies>
```
> **Note:** importing the parent POM also imports the framework dependency management used by that Mirage-SQL release. Review the selected line in [Compatibility](COMPATIBILITY.md). If the application uses a different framework baseline, declare module versions explicitly and validate the resolved dependency tree.

or in a Gradle based project add to your `build.gradle`:
```groovy
def mirageSqlVersion = 'REPLACE_WITH_VERSION'
implementation "vn.com.unit.miragesql:miragesql:${mirageSqlVersion}"
implementation "vn.com.unit.miragesql:miragesql-integration:${mirageSqlVersion}"
testImplementation "vn.com.unit.miragesql:miragesql-test:${mirageSqlVersion}"
```

### Building from source
Use the JDK and Maven versions required by the checked-out release. See [Compatibility](COMPATIBILITY.md) and the release POM, then run Maven from the repository root:

```
mvn clean install
mvn -pl mirage -am test
```

### Migration

Use [MIGRATION.md](MIGRATION.md) as the migration index. Major-version procedures are stored under `docs/migration/{from-major}-to-{to-major}.md`.

The [4.x to 5.x guide](docs/migration/4-to-5.md) currently defines the migration controls but intentionally does not invent `5.x` coordinates or breaking changes. Complete those details only from an immutable `5.x` source baseline and released artifacts.

### Example

This is a simple example of a SQL template:

```sql
SELECT * FROM BOOK
/*BEGIN*/
  WHERE
  /*IF author != null */
        AUTHOR = /*author*/'Naoki Takezoe'
  /*END*/
  /*IF minPrice != null */
    AND PRICE >= /*minPrice*/20
  /*END*/
  /*IF maxPrice != null */
    AND PRICE <= /*maxPrice*/100
  /*END*/
/*END*/
ORDER BY BOOK_ID ASC
```

With **`Mirage-SQL`** you can embed variables or conditions using `special` SQL comments, so it's a plain SQL that can be run with any SQL client tool directly.
This feature used in the Mirage's SQL template it's called [2Way SQL](https://github.com/unit-corp/mirage/wiki/2WaySQL).

### Links
 - **Source code and issues** of this fork: [github.com/unit-corp/mirage](https://github.com/unit-corp/mirage).
 - A **detailed documentation** is provided in the [Wiki](https://github.com/unit-corp/mirage/wiki).
 - **Release notes** of this fork: [CHANGELOG.md](CHANGELOG.md) and the [Wiki Releases page](https://github.com/unit-corp/mirage/wiki/Releases).
 - **Compatibility and lifecycle:** [COMPATIBILITY.md](COMPATIBILITY.md).
 - **Security policy:** [SECURITY.md](SECURITY.md).
 - **Contributing and releasing:** [CONTRIBUTING.md](CONTRIBUTING.md) and [RELEASING.md](RELEASING.md).
 - Mirage-SQL also has support for other **JVM based languages**:
   - [Scala language](http://www.scala-lang.org/) support - [Mirage-SQL Scala](https://github.com/mirage-sql/mirage-scala)

### License
This project is licensed under the Apache License, Version 2.0. See the [LICENSE](LICENSE) file.

This product is a fork of [Mirage-SQL](https://github.com/mirage-sql/mirage), originally developed by Naoki Takezoe and contributors, and is maintained by UNIT Technology Corporation. Copyright and attribution notices are in the [NOTICE](NOTICE) file. The original copyright notices in the source files are kept unchanged.
