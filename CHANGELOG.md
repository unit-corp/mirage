# Changelog

## Release lines

The version number selects the line, and the major number matches the Spring Boot generation.

| Line | Group | Artifacts | Java package | Spring Boot | Spring Framework | Minimum Java | Availability |
|------|-------|-----------|--------------|-------------|------------------|--------------|--------------|
| `2.1.2` (original, last upstream release) | `com.miragesql` | `miragesql`, `miragesql-integration`, `miragesql-test` | `com.miragesql` | n/a (plain Spring Framework) | 5.x | 8 | Maven Central |
| `3.0.x` (UNIT internal fork) | `com.miragesql` | `miragesql`, `miragesql-integration`, `miragesql-test` | `com.miragesql` | 3.x | 6.x | 17 | Internal (UNIT private repository) |
| `4.0.x` (legacy, UNIT-maintained) | `vn.com.unit.miragesql` | `miragesql-legacy`, `miragesql-integration-legacy`, `miragesql-test-legacy` | `com.miragesql` | 4.x | 7.x | 17 | Maven Central |
| `4.1+` (current, UNIT active development) | `vn.com.unit.miragesql` | `miragesql`, `miragesql-integration`, `miragesql-test` | `vn.com.unit.miragesql` | 4.x | 7.x | 17 | Maven Central |

`2.1.2` is the last upstream release published on Maven Central before UNIT forked the project; later versions are UNIT forks. Applications that still use the `com.miragesql` package and want UNIT maintenance should use the legacy artifacts (see the maintenance/legacy section below).

## Unreleased (maintenance/legacy)

This branch is the UNIT-maintained **legacy** line for applications that still run the original `com.miragesql` package name. It receives maintenance fixes only; no new features are planned.

### Changed

- Maven group changed from `com.miragesql` to **`vn.com.unit.miragesql`**.
- Artifact names now carry a `-legacy` suffix: `miragesql-parent-legacy`, `miragesql-legacy`, `miragesql-integration-legacy`, `miragesql-test-legacy`.
- The Java package name is unchanged (`com.miragesql`), so consumer source and configuration need no change.
- Minimum Java version set to **17**; the library is built against Spring Boot 4.
- Project metadata (organization, SCM, developers, license) and Maven Central publishing setup (sources, javadoc, GPG signing, central publishing plugin) added to the parent pom.

### Migration

- From the original Mirage-SQL: change the dependency groupId to `vn.com.unit.miragesql` and add `-legacy` to the artifactId. Keep imports and configuration as they are.
- From legacy `4.0.1` to `4.1+`: remove the `-legacy` suffix from the artifactIds and replace the `com.miragesql` package with `vn.com.unit.miragesql` in code and configuration. See the README, section "From legacy `4.0.1` to the current line `4.1+`", for the steps.
- Recommended: move to the actively developed line. Replace the `com.miragesql` package with `vn.com.unit.miragesql` in imports and configuration, and use the artifactIds without the `-legacy` suffix (`miragesql`, `miragesql-integration`, `miragesql-test`).

## 4.0.1

### Changed

- Spring Boot upgraded from 3.4.0 to **4.1.2** (Spring Framework 6.2.0 to 7.0.10, JUnit Platform 1.11.3 to 6.0.3).
- Project version moved from 3.0.1 to **4.0.1** for all modules. Group and artifact names are unchanged.

### Compatibility

- Minimum Java version is unchanged: **Java 17**.
- No public class or method of the library changed; no source change is required on the consumer side other than moving the application to Spring Boot 4.
- Existing SQL templates, Spring wiring and transaction handling behave as in 3.0.1 (all 80 existing tests pass unchanged).
- This release requires Spring Boot 4 / Spring Framework 7. Applications that stay on Spring Boot 3 must keep using the `3.0.x` line.

### Dependencies carried over from the 3.0.2 work

- OGNL 3.4.17 (was 3.2.4), and JUnit 4 tests run through `junit-vintage-engine` on the JUnit Platform.

### Notes

- `spring-boot-starter-tomcat` (used by the module that provides the servlet filter and listener) now brings more Spring Boot modules transitively than it did on Spring Boot 3.
