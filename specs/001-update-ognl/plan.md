# Implementation Plan: Update OGNL Library to Latest Release

> **Update (maintenance/legacy branch)**: this document records the OGNL update and is kept as history. The minimum Java version is now **17** (not 21) and the artifacts are now named with a `-legacy` suffix under the group `vn.com.unit.miragesql`. See `CHANGELOG.md`.


**Branch**: `001-update-ognl` | **Date**: 2026-10-26 | **Spec**: [spec.md](spec.md)

**Input**: Feature specification from `/specs/001-update-ognl/spec.md`

## Summary

Upgrade the `ognl:ognl` dependency of the `mirage` module from 3.2.4 to the latest stable release, 3.4.17 (Maven Central's `latest`/`release` marker is 3.5.0-BETA10, which is excluded as a pre-release). Preserve all SQL-template expression behavior. Source changes are confined to the 4 classes that touch the OGNL API directly, and only where the new version forces them (see [research.md](research.md)).

## Technical Context

**Language/Version**: Java (`java.version` = 17 in root `pom.xml` on `maintenance/legacy`; it was 21 in release 3.0.1)

**Primary Dependencies**: `ognl:ognl` 3.2.4 → 3.4.17 (declared only in `mirage/pom.xml`); Spring Boot parent 3.4.0; Javassist 3.29.2-GA

**Storage**: N/A

**Testing**: JUnit 4 via Maven (`mvn test` per module); existing suites in `mirage`, `miragesql-test`, `miragesql-integration`

**Target Platform**: JVM library (multi-module Maven build: `mirage`, `miragesql-test`, `miragesql-integration`)

**Project Type**: library

**Performance Goals**: No regression in expression parse/evaluate time (not measured beyond test-suite runtime)

**Constraints**: Behavior-preserving; must keep non-public member access in templates; no unrelated refactoring (FR-007)

**Scale/Scope**: 1 POM line, up to 4 source files (`OgnlUtil`, `SqlContextImpl`, `SqlContextPropertyAccessor`, and `OgnlRuntimeException` only if the exception API changed)

## Constitution Check

`.specify/memory/constitution.md` is still the unfilled template, so there are no ratified principles to gate against. Result: PASS (no applicable gates). Self-imposed gates: behavior-preserving, minimal diff, full suite green.

Post-design re-check: PASS, design adds no new projects, abstractions or interfaces.

## Project Structure

### Documentation (this feature)

```text
specs/001-update-ognl/
├── plan.md
├── research.md
├── data-model.md
├── quickstart.md
└── checklists/requirements.md
```

(No `contracts/`: the library's public API is unchanged; `OgnlUtil` keeps its method signatures.)

### Source Code (repository root)

```text
pom.xml                                  # parent (java.version 17; 21 originally), + junit-vintage-engine test dependency
mirage/
├── pom.xml                              # ognl version bump (line ~75)
└── src/main/java/com/miragesql/miragesql/
    ├── util/OgnlUtil.java               # context construction, DefaultMemberAccess
    ├── parser/SqlContextImpl.java       # OgnlRuntime.setPropertyAccessor registration
    ├── parser/SqlContextPropertyAccessor.java
    └── exception/OgnlRuntimeException.java
miragesql-test/                          # regression
miragesql-integration/                   # regression
```

**Structure Decision**: Existing multi-module layout; the change is local to the `mirage` module with the other modules used as regression checks.

## Complexity Tracking

No violations.
