# Implementation Plan: Upgrade to Latest Spring Boot 4

**Branch**: `002-upgrade-spring-boot-4` | **Date**: 2026-10-26 | **Spec**: [spec.md](spec.md)

**Input**: Feature specification from `/specs/002-upgrade-spring-boot-4/spec.md`

## Summary

Move the multi-module Maven project from Spring Boot 3.4.0 to the latest stable Spring Boot 4 release, 4.1.2 (Spring Framework 7.0.10), by changing the single `spring.boot.version` property and fixing only what the build and tests report. Java 21 stays. The project version moves from 3.0.1 to **4.0.1** with the same group and artifact names, while the Spring Boot 3 line stays maintained as `3.0.x` on its own `support/3.x` branch (see Release Line Naming). Research (see [research.md](research.md)) found that the Spring API surface used is small (`SpringConnectionProvider` plus a Servlet API dependency), and that the main risks are test tooling versions (Spock and the JUnit Platform) and managed third-party jumps (HikariCP, H2), which the existing suite will reveal.

## Technical Context

**Language/Version**: Java 21 (`java.version`); Spring Boot 4.1.2 requires 17+

**Primary Dependencies**: Spring Boot 4.1.2 (parent import), Spring Framework 7.0.10, OGNL 3.4.17, javassist 3.29.2-GA, HikariCP and commons-dbcp2 (optional), Guice 5.0.1 (optional)

**Storage**: Relational databases through JDBC; H2 and HSQLDB in tests

**Testing**: Surefire on the JUnit Platform with `junit-vintage-engine` for JUnit 4 tests; Spock 2.4-M1-groovy-4.0 with Groovy 4.0.17 for `*Spec.groovy`; Mockito

**Target Platform**: JVM library consumed by Spring, Guice or plain Java applications

**Project Type**: Multi-module Maven library (`mirage`, `miragesql-integration`, `miragesql-test`)

**Performance Goals**: N/A (no behavior or performance change intended)

**Constraints**: Minimal change (spec FR-006); previously delivered upgrades must keep working (FR-008)

**Scale/Scope**: 3 modules; 31 Java/Groovy files import Spring, Jakarta, JDBC pool or JUnit APIs, but only 5 use Spring or Servlet APIs

## Constitution Check

`.specify/memory/constitution.md` is still the unfilled template, so there are no ratified principles to gate on. Applied project-level rules instead: keep the change minimal, keep the Java 21 baseline, keep the test suite as the regression oracle. No violations. Re-checked after Phase 1 design: unchanged.

## Project Structure

### Documentation (this feature)

```text
specs/002-upgrade-spring-boot-4/
├── plan.md              # This file
├── research.md          # Phase 0 output
├── data-model.md        # Phase 1 output
├── quickstart.md        # Phase 1 output
├── checklists/
│   └── requirements.md
└── tasks.md             # Phase 2 output (/speckit-tasks - NOT created by /speckit-plan)
```

No `contracts/` directory: the library's public API is not changed by this feature, so no new or changed interface contract is defined.

### Source Code (repository root)

```text
pom.xml                              # spring.boot.version (3.4.0 -> 4.1.2); project version 3.0.1 -> 4.0.1
mirage/
├── pom.xml                          # parent version 4.0.1; spring-boot-starter-tomcat, Spock/Groovy pins
└── src/{main,test}/                 # servlet filter/listener; Spock specs
miragesql-integration/
├── pom.xml                          # parent version 4.0.1; spring-core, spring-context, spring-jdbc, spring-aop
└── src/{main,test}/                 # SpringConnectionProvider and its tests
miragesql-test/
└── pom.xml                          # parent version 4.0.1
```

**Structure Decision**: Existing multi-module layout is kept. Changes are expected in the root POM and, only if the build or tests require it, the module POMs and the Spring-integration code and tests.

## Release Line Naming

Both lines keep the group `com.miragesql` and the artifacts `miragesql`, `miragesql-integration` and `miragesql-test`; the version number alone separates them. The major number matches the Spring Boot generation.

| Line | Spring Boot | Version series | Branches |
|------|-------------|----------------|----------|
| Old | 3.4.x | `3.0.x` (3.0.1 released; next 3.0.2, 3.0.3...) | `release/3.0.1` (exists); `release/3.0.2` (from `feature/upgrade-libs-3.0.2`); long-lived `support/3.x` cut from `release/3.0.2` |
| New | 4.1.2 | `4.0.x`, starting at `4.0.1` | `feature/spring-boot-4` (from `feature/upgrade-libs-3.0.2`, so it already has Java 21 and OGNL 3.4.17) merged into `release/4.0.1`, tagged `4.0.1`; `main` follows this line afterwards |

Notes:

- The branch name differs from the spec directory name `002-upgrade-spring-boot-4` by design (spec Assumptions); the git branches in the table are not created by this plan.
- Existing local branch `chore/version-3.0.2` and the remote branches are left as they are; reconciling them with `release/3.0.2` is a maintainer decision at release time.
- `distributionManagement` points at the internal releases repository; a published version is immutable there, so a `3.0.x` number must never be reused by the 4.x line (guaranteed by the different major).

## Approach

1. Capture the baseline test counts on the current commit (quickstart step 1).
2. Change `spring.boot.version` to 4.1.2 and the project version to 4.0.1 (root POM `version` and the `parent` version in each module POM; `mirage.version` follows `project.version`); build; fix reported compile and test failures in order (POM dependency mapping first, then source, then test tooling such as Spock).
3. Verify the dependency tree has no Spring 6.x / Boot 3.x artifacts and a single version of each pinned library.
4. Run the vulnerability review and record the result.
5. Record the change notes (versions, Java baseline, line-to-Spring-Boot pairing, any consumer action).
6. At delivery, a maintainer creates the branches and tag from the Release Line Naming table and publishes; this is outside the code change.

## Complexity Tracking

No violations to justify.
