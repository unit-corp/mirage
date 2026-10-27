---

> **Update (maintenance/legacy branch)**: this document records the original `4.0.1` work and is kept as history. On the `maintenance/legacy` branch the artifacts are `miragesql-parent-legacy`, `miragesql-legacy`, `miragesql-integration-legacy` and `miragesql-test-legacy` under the group `vn.com.unit.miragesql`, the Java package stays `com.miragesql`, the minimum Java version is **17** (not 21), and the line is published to Maven Central. See `CHANGELOG.md` ("Release lines" and "Unreleased") for the current state.


description: "Task list for upgrading to Spring Boot 4 and releasing 4.0.1"
---

# Tasks: Upgrade to Latest Spring Boot 4

**Input**: Design documents from `/specs/002-upgrade-spring-boot-4/`

**Prerequisites**: plan.md, spec.md, research.md, data-model.md, quickstart.md

**Tests**: No new test tasks are requested. The existing suites in `mirage`, `miragesql-test` and `miragesql-integration` are the regression oracle. T016 adds or adapts a test only if a coverage gap or test-tooling incompatibility is found.

**Organization**: Grouped by user story. Target Spring Boot is **4.1.2** (4.2.0-RC1 is a pre-release and excluded); target project version is **4.0.1**; the Spring Boot 3 line stays `3.0.x`.

## Format: `[ID] [P?] [Story] Description`

- **[P]**: Can run in parallel (different files, no dependencies)
- **[Story]**: Which user story this task belongs to

## Path Conventions

Multi-module Maven build at the repository root: `mirage/`, `miragesql-test/`, `miragesql-integration/`. Run Maven from the repository root. Do the work on a local branch `feature/spring-boot-4` created from `feature/upgrade-libs-3.0.2` (T003); do not push or publish without the maintainer's go-ahead.

---

## Phase 1: Setup

**Purpose**: Establish a baseline before changing anything

- [X] T001 Run `mvn clean test` from the repository root on the current commit and record per-module pass/fail/skip counts (and any pre-existing failures) in `specs/002-upgrade-spring-boot-4/baseline.md`
- [X] T002 [P] Run `mvn dependency:tree -Dincludes=org.springframework,org.springframework.boot,org.junit.platform,org.spockframework,org.apache.groovy` and record the resolved versions per module in `specs/002-upgrade-spring-boot-4/baseline.md`
- [X] T003 Create local branch `feature/spring-boot-4` from `feature/upgrade-libs-3.0.2` (`git switch -c feature/spring-boot-4`). Do not push

---

## Phase 2: Foundational

**Purpose**: Confirm the target versions still hold before editing

- [X] T004 Re-check `https://repo.maven.apache.org/maven2/org/springframework/boot/spring-boot-starter-parent/maven-metadata.xml` and confirm 4.1.2 is still the newest non-milestone, non-RC version; if a newer stable exists, use it everywhere below and update `specs/002-upgrade-spring-boot-4/plan.md`, `research.md` and `data-model.md`

**Checkpoint**: Baseline recorded, branch ready, target version confirmed.

---

## Phase 3: User Story 1 - Builds and passes all tests on Spring Boot 4 (Priority: P1) MVP

**Goal**: All 3 modules build and every previously passing test still passes on Spring Boot 4.1.2.

**Independent Test**: `mvn clean test` from the repository root matches the T001 baseline counts, and the dependency tree has no Spring 6.x / Boot 3.x artifacts.

- [X] T005 [US1] In `pom.xml` change property `spring.boot.version` from `3.4.0` to `4.1.2` (`spring-boot-maven-plugin.version` follows it). Do not change any other version in this task
- [X] T006 [US1] Run `mvn clean test-compile` and fix only reported POM resolution errors, mapping removed or renamed Boot 3 dependencies to their Boot 4 equivalents in `pom.xml`, `mirage/pom.xml`, `miragesql-integration/pom.xml` or `miragesql-test/pom.xml` (research R3, R5 items 4 and 6). Keep `spring-boot-starter-tomcat` in `mirage/pom.xml` unless it no longer resolves
- [X] T007 [US1] Check the explicitly pinned libraries for compatibility with Spring Boot 4 and Java 17: `ognl` 3.4.17 and `javassist` 3.29.2-GA and `commons-dbcp2` 2.11.0 in `mirage/pom.xml`, `guice` 5.0.1 and `cglib` 3.3.0 in `miragesql-integration/pom.xml`, `cglib` in `miragesql-test/pom.xml`. Keep each pin unless the build or a test shows a conflict with the Boot-managed set; record any change and its reason in `specs/002-upgrade-spring-boot-4/research.md` under a new section "Upgrade findings"
- [X] T008 [US1] Fix only reported Java compile errors from removed or relocated Spring APIs, starting with `miragesql-integration/src/main/java/com/miragesql/miragesql/integration/spring/SpringConnectionProvider.java`; no unrelated refactoring
- [X] T009 [US1] Run `mvn clean test`. If Spock specs (`mirage/src/test/groovy/**/*Spec.groovy`) fail to load on JUnit Platform 6, change the Spock version in `mirage/pom.xml` from `2.4-M1-groovy-4.0` to `2.4-groovy-4.0` and keep Groovy 4.0.17 (research R5 item 1)
- [X] T010 [US1] If JUnit 4 tests are not discovered, make sure `junit-vintage-engine` in `pom.xml` resolves to a version compatible with the Boot-managed JUnit Platform (research R5 item 2)
- [X] T011 [US1] For each remaining test failure caused by a managed-library jump (HikariCP 7, H2 2.4, HSQLDB, Mockito 5.23, commons-dbcp2), decide per failure: adapt the code or test minimally, or pin the old version in the affected module POM with a comment. Record each decision and reason in `specs/002-upgrade-spring-boot-4/research.md` under a new section "Upgrade findings"
- [X] T012 [US1] Run `mvn clean test` again and compare the per-module counts with `specs/002-upgrade-spring-boot-4/baseline.md`; they must match with zero changes to expected SQL output. Also confirm the earlier upgrades still work: the build runs on Java 17+ (Java 21 originally), `mvn dependency:tree -Dincludes=ognl:ognl` shows a single `ognl` 3.4.17, and JUnit 4 tests are still executed through `junit-vintage-engine` (FR-008)
- [X] T013 [US1] Run `mvn dependency:tree -Dincludes=org.springframework,org.springframework.boot` and confirm every Spring artifact is on 7.0.x / 4.1.2 and none on 6.x / 3.x; record the result in `specs/002-upgrade-spring-boot-4/research.md` "Upgrade findings"

**Checkpoint**: User Story 1 done; the project builds and tests are green on Spring Boot 4.1.2 (MVP).

---

## Phase 4: User Story 2 - Spring integration keeps working for consumers (Priority: P2)

**Goal**: Mirage SQL's Spring wiring, transactions and data access behave as before.

**Independent Test**: The Spring integration tests pass, and a review confirms no consumer-facing API or configuration convention changed.

- [X] T014 [US2] Run `mvn -pl miragesql-integration -am test` and confirm `SpringConnectionProviderTest` and `TransactionInterceptorTest` pass with results identical to the baseline (wiring, query, insert/update, commit, rollback). If the XML context in `miragesql-integration/src/test/resources/com/miragesql/miragesql/integration/spring/applicationContext.xml` fails to resolve schemas, update only its schema locations
- [X] T015 [P] [US2] Run `git diff feature/upgrade-libs-3.0.2 -- miragesql-integration/src/main mirage/src/main` and confirm no public class or method signature of the library changed; if one did, record it as a consumer action in `specs/002-upgrade-spring-boot-4/research.md` "Upgrade findings" (feeds T020)
- [X] T016 [US2] Only if T014 or T015 shows a coverage gap (for example transaction rollback not covered by an existing test), add a minimal test next to `miragesql-integration/src/test/java/com/miragesql/miragesql/integration/spring/SpringConnectionProviderTest.java`; otherwise tick this task `[X]` with the note "N/A, no gap found" — N/A, no gap found

**Checkpoint**: User Stories 1 and 2 both verified.

---

## Phase 5: User Story 3 - Release line, version and notes (Priority: P3)

**Goal**: Release `4.0.1` with unchanged group and artifact names, keep the `3.0.x` line separate, and document the pairing with Spring Boot.

**Independent Test**: Every module reports project version `4.0.1`, the notes state the line-to-Spring-Boot pairing, and a build of the 3.x line still resolves Spring Boot 3.

- [X] T017 [US3] In `pom.xml` set `<version>` of `miragesql-parent` from `3.0.1` to `4.0.1`; keep `groupId` `com.miragesql` and all artifact ids unchanged
- [X] T018 [P] [US3] In `mirage/pom.xml`, `miragesql-integration/pom.xml` and `miragesql-test/pom.xml` set the `<parent>` `<version>` to `4.0.1`
- [X] T019 [US3] Run `mvn clean verify` and `mvn help:evaluate -Dexpression=project.version -q -DforceStdout` in each module; confirm `4.0.1` everywhere, that `mirage.version` resolves to `4.0.1`, and that the built jars are named `miragesql-4.0.1`, `miragesql-integration-4.0.1` and `miragesql-test-4.0.1`
- [X] T020 [US3] Create `CHANGELOG.md` at the repository root (no changelog exists yet) with an entry for `4.0.1`: Spring Boot 3.4.0 to 4.1.2, Spring Framework 7.0.10, Java baseline (now 17 on `maintenance/legacy`, originally 21), any behavior differences or consumer actions from `research.md` "Upgrade findings", and a table pairing `3.0.x` with Spring Boot 3.4 and `4.0.x` with Spring Boot 4.1 (FR-009, FR-013)
- [X] T021 [P] [US3] Run the vulnerability check: `mvn org.owasp:dependency-check-maven:aggregate` if it works with the pinned plugin (7.4.1, note `main.basedir/security/project-suppression.xml` does not exist in the repo); otherwise review GHSA/NVD for Spring Boot 4.1.2 and Spring Framework 7.0.10. Record the result in `specs/002-upgrade-spring-boot-4/research.md` R7 (FR-010, SC-006)
- [X] T022 [US3] Verify SC-007 without touching the working tree: `git worktree add` a temporary checkout of `release/3.0.1`, run `mvn dependency:tree -Dincludes=org.springframework.boot` there, confirm Spring Boot 3 and version `3.0.1`, then remove the worktree. Record the result in `specs/002-upgrade-spring-boot-4/research.md` "Upgrade findings"
- [X] T023 [US3] Write a short release checklist in `specs/002-upgrade-spring-boot-4/release-steps.md` for the maintainer, per plan "Release Line Naming": create `release/3.0.2` from `feature/upgrade-libs-3.0.2` and `support/3.x` from it; merge `feature/spring-boot-4` into `release/4.0.1`; tag `4.0.1`; publish to the internal releases repository; reconcile the existing `chore/version-3.0.2` branch. Do not run any of these steps

**Checkpoint**: All user stories complete.

---

## Phase 6: Polish & Cross-Cutting

- [X] T024 Update the Spring Boot version and the project version in `README.md` if it mentions them (check with a search first); otherwise skip — N/A, README has no version or Spring references
- [X] T025 Walk through `specs/002-upgrade-spring-boot-4/quickstart.md` steps 1 to 8 end to end and tick every success criterion SC-001 to SC-007 in a short summary appended to `baseline.md`

---

## Dependencies & Execution Order

- Phase 1 then Phase 2 (T004 needs no code), then US1, then US2 and US3.
- **US1** (T005 to T013) is strictly sequential: each task builds on the previous fix.
- **US2** needs US1 green; T015 can run in parallel with T014.
- **US3** needs US1 green; T017 then T018 and T019; T020 needs the findings from US1 and US2; T021 and T022 are independent of each other.
- T023 can be written at any time after T003 but is placed last so it reflects the final state.

## Parallel Opportunities

- T002 with T001 (read-only, different output sections).
- T015 with T014.
- T018 with T017 (different files), T021 with T022.

## Implementation Strategy

1. **MVP**: Phases 1 to 3. Green build and tests on Spring Boot 4.1.2 under the current `3.0.1` version number; this proves the upgrade before any naming change.
2. Add US2 verification, then US3 version and notes, then the polish pass.
3. Stop before any push, tag or publish; those are maintainer steps (T023).
