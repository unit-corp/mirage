Mirage-SQL (legacy) [![Maven Central](https://img.shields.io/maven-central/v/vn.com.unit.miragesql/miragesql-legacy)](https://central.sonatype.com/artifact/vn.com.unit.miragesql/miragesql-legacy)
======

> **Maintenance only.** This legacy line is maintained by UNIT Technology Corporation for applications that still use the `com.miragesql` package. New features go to the current line (`vn.com.unit.miragesql`, artifacts without the `-legacy` suffix). See [Migration](#migration).

**`Mirage-SQL`** is an easy and powerful SQL-centric database access library for Java (or JVM based languages) which provides dynamic SQL templates in plain SQL files.

This project is a fork of [Mirage-SQL](https://github.com/mirage-sql/mirage) maintained by UNIT Technology Corporation since 2025.

Requires **Java 17+**; built against Spring Boot 4 / Spring Framework 7. See the [Changelog](CHANGELOG.md) for the release lines.

### Project structure
This is a multi-module Maven build. The root `miragesql-parent-legacy` is the parent POM (`pom` packaging); it holds the shared configuration and the managed versions of the modules below. You do not declare it in your application, Maven resolves it automatically when it resolves a module.

Module|Artifact|Description
---   |---     |---
**Mirage-SQL**|`miragesql-legacy`|The core library.
**Mirage-SQL Test**|`miragesql-test-legacy`|The testing functionality.
**Mirage-SQL Integration**|`miragesql-integration-legacy`|The integration with [Spring](https://projects.spring.io/spring-framework/), [Guice](https://github.com/google/guice) and [Seasar2](http://www.seasar.org/en/).

All artifacts use the group `vn.com.unit.miragesql` and the same version (`4.0.1`). The upstream **Mirage-SQL Tools** module (`miragesql-tools`) is not part of this repository.

### Using
You can get **`Mirage-SQL`** (legacy) from the Maven Central repository. Add the modules you need into your `pom.xml`, with the same version for all of them.

```xml
<dependency>
    <groupId>vn.com.unit.miragesql</groupId>
    <artifactId>miragesql-legacy</artifactId>
    <version>4.0.1</version>
</dependency>

<!-- optional: Spring, Guice or Seasar2 integration -->
<dependency>
    <groupId>vn.com.unit.miragesql</groupId>
    <artifactId>miragesql-integration-legacy</artifactId>
    <version>4.0.1</version>
</dependency>

<!-- optional: testing functionality -->
<dependency>
    <groupId>vn.com.unit.miragesql</groupId>
    <artifactId>miragesql-test-legacy</artifactId>
    <version>4.0.1</version>
    <scope>test</scope>
</dependency>
```
To declare the version once, import the parent POM in the `dependencyManagement` of your `pom.xml`, then omit the version on each module:

```xml
<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>vn.com.unit.miragesql</groupId>
            <artifactId>miragesql-parent-legacy</artifactId>
            <version>4.0.1</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
    </dependencies>
</dependencyManagement>

<dependencies>
    <dependency>
        <groupId>vn.com.unit.miragesql</groupId>
        <artifactId>miragesql-legacy</artifactId>
    </dependency>
</dependencies>
```
> **Note:** the parent POM also imports the Spring Boot 4 dependency management, so this import manages Spring, SLF4J and other library versions in your project too (for example Spring Framework 7.0.x). If your application is on another Spring generation, declare the versions explicitly as shown above instead.

or in a Gradle based project add to your `build.gradle`:
```groovy
implementation 'vn.com.unit.miragesql:miragesql-legacy:4.0.1'
implementation 'vn.com.unit.miragesql:miragesql-integration-legacy:4.0.1'
testImplementation 'vn.com.unit.miragesql:miragesql-test-legacy:4.0.1'
```

### Building from source
Requires JDK 17+ and Maven 3.9.x. Run Maven from the repository root:

```
mvn clean install
mvn -pl mirage -am test
```
The first command builds and tests all modules; the second builds and tests only the core module and what it depends on. Publishing to Maven Central uses the `central-publishing` profile (it needs the GPG key and the Central credentials configured on the machine).

### Migration

Artifact mapping. The **Current** column is the line UNIT keeps developing new features on, and is the recommended target for your applications:

Module|Original (upstream)|Legacy (maintenance only)|Current (UNIT, active development)
---   |---                |---                      |---
Group|`com.miragesql`|`vn.com.unit.miragesql`|`vn.com.unit.miragesql`
Java package|`com.miragesql`|`com.miragesql`|`vn.com.unit.miragesql`
Parent|-|`miragesql-parent-legacy`|`miragesql-parent`
Core|`miragesql`|`miragesql-legacy`|`miragesql`
Test|`miragesql-test`|`miragesql-test-legacy`|`miragesql-test`
Integration|`miragesql-integration`|`miragesql-integration-legacy`|`miragesql-integration`

**From the original Mirage-SQL (`com.miragesql`, up to 2.1.2) to this legacy line**: no code change. Change the dependency groupId to `vn.com.unit.miragesql`, add the `-legacy` suffix to the artifactId, and keep your imports and configuration (the package stays `com.miragesql`). Check that your runtime is Java 17+ and compatible with Spring Boot 4.

**Recommended: to the current line (UNIT active development)**: replace the `com.miragesql` package with `vn.com.unit.miragesql` in your imports and configuration, and use the artifactIds without the `-legacy` suffix.

If you are updating your application from a previous **`Mirage-SQL`** version, see the [Migration Guide](https://github.com/unit-corp/mirage/wiki/Migration-Guide).

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

### Dependency updates
- **OGNL 3.2.4 -> 3.4.17** (`mirage` module). No behavior differences were found in the test suite (SQL generation, bind variables, `has_` arguments, error reporting, non-public member access). OGNL 3.4 changed the `MemberAccess` and `PropertyAccessor` method signatures from `Map` to `OgnlContext`; `OgnlUtil.DefaultMemberAccess` and `SqlContextPropertyAccessor` were adapted. Code outside this project that extends or calls these classes with a `Map` must switch to `OgnlContext`.
- The root `pom.xml` has `junit-vintage-engine` (test scope), because surefire runs on the JUnit Platform and previously skipped all JUnit 4 tests.

### Links
 - **Source code and issues** of this fork: [github.com/unit-corp/mirage](https://github.com/unit-corp/mirage).
 - A **detailed documentation** is provided in the [Wiki](https://github.com/unit-corp/mirage/wiki).
 - **Release notes** of this fork: [CHANGELOG.md](CHANGELOG.md) and the [Wiki Releases page](https://github.com/unit-corp/mirage/wiki/Releases).
 - Mirage-SQL also has support for other **JVM based languages**:
   - [Scala language](http://www.scala-lang.org/) support - [Mirage-SQL Scala](https://github.com/mirage-sql/mirage-scala)

### License
This project is licensed under the Apache License, Version 2.0. See the [LICENSE](LICENSE) file.

This product is a fork of [Mirage-SQL](https://github.com/mirage-sql/mirage), originally developed by Naoki Takezoe and contributors, and is maintained by UNIT Technology Corporation. Copyright and attribution notices are in the [NOTICE](NOTICE) file; the original copyright notices in the source files are kept unchanged.
