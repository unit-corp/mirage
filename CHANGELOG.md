# Changelog

## Release lines

The version number selects the line, and the major number matches the Spring Boot generation.

| Line | Group | Artifacts | Java package | Spring Boot | Spring Framework | Minimum Java | Availability |
|------|-------|-----------|--------------|-------------|------------------|--------------|--------------|
| `2.1.2` (original, last upstream release) | `com.miragesql` | `miragesql`, `miragesql-integration`, `miragesql-test` | `com.miragesql` | n/a (plain Spring Framework) | 5.x | 8 | Maven Central |
| `3.0.x` (UNIT internal fork) | `com.miragesql` | `miragesql`, `miragesql-integration`, `miragesql-test` | `com.miragesql` | 3.x | 6.x | 17 | Internal (UNIT private repository) |
| `4.0.x` (legacy, UNIT-maintained) | `vn.com.unit.miragesql` | `miragesql-legacy`, `miragesql-integration-legacy`, `miragesql-test-legacy` | `com.miragesql` | 4.x | 7.x | 17 | Maven Central |
| `4.1+` (current, UNIT active development) | `vn.com.unit.miragesql` | `miragesql`, `miragesql-integration`, `miragesql-test` | `vn.com.unit.miragesql` | 4.x | 7.x | 17 | Maven Central |

`2.1.2` is the last upstream release published on Maven Central before UNIT forked the project; later versions are UNIT forks. The legacy line is for applications that still use the `com.miragesql` package; it is built from the `maintenance/legacy` branch and receives maintenance fixes only.

## 4.1.1

First release of the current line (`4.1+`). It has the same behavior as the legacy `4.0.x` line; the differences are the Java package and the artifact names.

### Changed

- Java package renamed from `com.miragesql` to **`vn.com.unit.miragesql`** (for example `com.miragesql.miragesql.SqlManager` is now `vn.com.unit.miragesql.miragesql.SqlManager`).
- Artifact names have no `-legacy` suffix: `miragesql-parent`, `miragesql`, `miragesql-test`, `miragesql-integration`. The group stays `vn.com.unit.miragesql`.
- Project version for all modules is **4.1.1**.
- Spring Boot 4.1.2 (Spring Framework 7.0.x). Minimum Java version stays **17**.
- OGNL 3.4.17, Guice 7.0.0, Guava 33.7.2-jre (CVE fixes), HikariCP 7.1.0.
- Test stack: JUnit Platform 6.1.3, Mockito 5.24.0, Groovy 6.0.0 and Spock 2.5-groovy-6.0. Spock specs now run by default; the opt-in `spock` profile was removed. JUnit 4 tests run through `junit-vintage-engine`.
- Build: `git-commit-id-plugin` replaced by `git-commit-id-maven-plugin` 9.2.0; plugin versions updated (surefire 3.6.1, gmavenplus 5.1.0, deploy 3.2.0, source 3.4.0, javadoc 3.12.0, gpg 3.2.8, central-publishing 0.12.0).
- `LICENSE` and `NOTICE` are packaged in `META-INF` of every jar.
- Project metadata (organization, SCM, issue management, developers, original contributors) is declared in the parent POM only and inherited by the modules.

### Dependencies

- `mirage`: `jakarta.servlet-api` (provided) replaces `spring-boot-starter-web`; `slf4j-api` is a compile dependency; HikariCP is compile and optional.
- `miragesql-integration`: Spring (`spring-context`, `spring-jdbc`) and Guice are compile and optional dependencies instead of provided; the Guava dependency was removed.
- The parent POM no longer adds Lombok, `slf4j-api` and `logback-classic` to every module.

### Migration

- From legacy `4.0.x`: remove the `-legacy` suffix from the artifactIds and replace the `com.miragesql` package with `vn.com.unit.miragesql` in code and configuration. See the README, section "From legacy `4.0.x` to the current line `4.1+`", for the steps.
- From the original Mirage-SQL (`2.1.2`): change the groupId to `vn.com.unit.miragesql`, move to `4.1.1`, and rename the package as above.

## 4.0.1 (legacy line)

Published from the `maintenance/legacy` branch with artifacts `miragesql-parent-legacy`, `miragesql-legacy`, `miragesql-test-legacy` and `miragesql-integration-legacy`. The Java package is unchanged (`com.miragesql`).

### Changed

- Maven group changed from `com.miragesql` to `vn.com.unit.miragesql`; artifact names carry a `-legacy` suffix.
- Spring Boot upgraded from 3.4.0 to **4.1.2** (Spring Framework 6.2.0 to 7.0.10, JUnit Platform 1.11.3 to 6.0.3).
- Project version moved from 3.0.1 to **4.0.1** for all modules.
- Minimum Java version is unchanged: **Java 17**.
- OGNL 3.4.17 (was 3.2.4), and JUnit 4 tests run through `junit-vintage-engine` on the JUnit Platform.

### Compatibility

- No public class or method of the library changed; no source change is required on the consumer side other than moving the application to Spring Boot 4.
- This release requires Spring Boot 4 / Spring Framework 7. Applications that stay on Spring Boot 3 must keep using the `3.0.x` line.

### Notes

- `spring-boot-starter-tomcat` (used by the module that provides the servlet filter and listener) brings more Spring Boot modules transitively than it did on Spring Boot 3.
