# Data Model: Upgrade to Latest Spring Boot 4

> **Update (maintenance/legacy branch)**: this document records the original `4.0.1` work and is kept as history. On the `maintenance/legacy` branch the artifacts are `miragesql-parent-legacy`, `miragesql-legacy`, `miragesql-integration-legacy` and `miragesql-test-legacy` under the group `vn.com.unit.miragesql`, the Java package stays `com.miragesql`, the minimum Java version is **17** (not 21), and the line is published to Maven Central. See `CHANGELOG.md` ("Release lines" and "Unreleased") for the current state.


This feature has no persistent data. The entities from the spec map to build and release artifacts.

## Spring Boot platform version

| Field | Value |
|-------|-------|
| Declared in | `pom.xml` property `spring.boot.version` (inherited by all modules) |
| Current | 3.4.0 |
| Target | 4.1.2 (latest stable on 2026-10-26; see research R1) |
| Also derived from it | `spring-boot-maven-plugin.version` (defaults to `${spring.boot.version}`) |

Rule: only stable releases are allowed; the version is declared once.

## Release line

| Field | Old line | New line |
|-------|----------|----------|
| Version series | `3.0.x` (3.0.1 released, next 3.0.2) | `4.0.x`, first `4.0.1` |
| Spring Boot | 3.4.x | 4.1.2 |
| Branches | `release/3.0.1`, `release/3.0.2`, `support/3.x` | `feature/spring-boot-4`, `release/4.0.1`, tag `4.0.1` |
| Declared in | `pom.xml` `<version>` and each module's `<parent>` version | same places, value `4.0.1` |

Rule: same group and artifact names in both lines; no version number belongs to both.

## Spring-integration module (`miragesql-integration`)

| Field | Value |
|-------|-------|
| Spring libraries | `spring-core`, `spring-context`, `spring-jdbc` (optional, compile); `spring-aop` (test) |
| Main class touching Spring | `SpringConnectionProvider` |
| Behavior to preserve | bean wiring, transaction-aware connection handling, commit and rollback |

## Companion Spring Data module

| Field | Value |
|-------|-------|
| Property | `spring-data-mirage.version` = 3.0.1.RELEASE |
| Referenced by modules | none |
| Handling | out of scope here (research R6) |

## Change notes

| Field | Value |
|-------|-------|
| Content | old and new Spring Boot versions, minimum Java version (17 on `maintenance/legacy`; originally 21), breaking changes or consumer actions |
| Location | to be decided in tasks (see plan: no changelog file exists yet) |
