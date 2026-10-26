---

description: "Task list for updating OGNL to the latest stable release"
---

# Tasks: Update OGNL Library to Latest Release

**Input**: Design documents from `/specs/001-update-ognl/`

**Prerequisites**: plan.md, spec.md, research.md, data-model.md, quickstart.md

**Tests**: No new test tasks are requested. The existing suites in `mirage`, `miragesql-test` and `miragesql-integration` are the regression oracle. One task (T012) adds a test only if a coverage gap is found.

**Organization**: Grouped by user story. Target version is **3.4.17** (latest stable; 3.5.0-BETA10 is a pre-release and excluded).

## Format: `[ID] [P?] [Story] Description`

- **[P]**: Can run in parallel (different files, no dependencies)
- **[Story]**: Which user story this task belongs to

## Path Conventions

Multi-module Maven build at the repository root: `mirage/`, `miragesql-test/`, `miragesql-integration/`. Run Maven from the repository root.

---

## Phase 1: Setup

**Purpose**: Establish a baseline before changing anything

- [X] T001 Run `mvn clean test` from the repository root and record per-module pass/fail/skip counts (and any pre-existing failures) in `specs/001-update-ognl/baseline.md`. Note: the original build ran 0 tests, so `junit-vintage-engine` was added to the root `pom.xml` first
- [X] T002 [P] Run `mvn dependency:tree -Dincludes=ognl:ognl` and record the current OGNL version(s) per module in `specs/001-update-ognl/baseline.md`

---

## Phase 2: Foundational

**Purpose**: Confirm the target version still holds before editing

- [X] T003 Re-check `https://repo1.maven.org/maven2/ognl/ognl/maven-metadata.xml` and confirm 3.4.17 is still the newest non-pre-release version; if a newer stable exists, use it everywhere below and update `specs/001-update-ognl/plan.md` and `research.md`

**Checkpoint**: Baseline recorded and target version confirmed

---

## Phase 3: User Story 1 - Library runs on the latest stable OGNL without behavior change (Priority: P1) 🎯 MVP

**Goal**: Project compiles and behaves identically on OGNL 3.4.17.

**Independent Test**: `mvn clean test` passes with results equal to the T001 baseline and no changes to expected SQL output in tests.

### Implementation for User Story 1

- [X] T004 [US1] Change the `ognl:ognl` `<version>` from `3.2.4` to `3.4.17` in `mirage/pom.xml` (the only place OGNL is declared)
- [X] T005 [US1] Run `mvn -pl mirage -am compile` and record any compile errors or deprecation warnings that come from `ognl.*` types (expected risk areas listed in `specs/001-update-ognl/research.md` R3)
- [X] T006 [US1] Fix only the errors from T005 in `mirage/src/main/java/com/miragesql/miragesql/util/OgnlUtil.java` (the `new OgnlContext(null, null, new DefaultMemberAccess(true))` construction, the inner `DefaultMemberAccess implements MemberAccess`, and `Ognl.getValue`/`Ognl.parseExpression` calls), keeping public method signatures unchanged and non-public member access working (FR-004)
- [X] T007 [P] [US1] Fix only the errors from T005 in `mirage/src/main/java/com/miragesql/miragesql/parser/SqlContextImpl.java` (`OgnlRuntime.setPropertyAccessor(SqlContext.class, ...)` registration). N/A: compiled unchanged on 3.4.17
- [X] T008 [P] [US1] Fix only the errors from T005 in `mirage/src/main/java/com/miragesql/miragesql/parser/SqlContextPropertyAccessor.java` (the `ObjectPropertyAccessor` override that `throws OgnlException`)
- [X] T009 [US1] Run `mvn -pl mirage -am test` and compare with the T001 baseline; for each new failure, determine whether it is caused by an OGNL behavior change (e.g. stricter expression restrictions) and fix it in the same files as T006-T008 without disabling OGNL security globally; list any unavoidable difference in `specs/001-update-ognl/behavior-differences.md`
- [X] T010 [US1] Verify FR-003: locate an existing test with an invalid expression under `mirage/src/test/java` that asserts `OgnlRuntimeException` with path and line number (see `mirage/src/main/java/com/miragesql/miragesql/exception/OgnlRuntimeException.java`)
- [X] T011 [US1] Verify FR-004: locate an existing test that evaluates a private/protected member through a template under `mirage/src/test/java`
- [X] T012 [US1] Only if T010 or T011 found no existing test, add a minimal JUnit test for the missing case under `mirage/src/test/java/com/miragesql/miragesql/util/` (e.g. `OgnlUtilTest.java`); otherwise mark this task N/A. Done: no existing test covered these, so `OgnlUtilTest` (7 tests) was added

**Checkpoint**: `mirage` builds and its tests match baseline on 3.4.17

---

## Phase 4: User Story 2 - Dependency is verifiably current and conflict-free (Priority: P2)

**Goal**: All modules build against exactly one OGNL version.

**Independent Test**: Dependency tree shows only `ognl:ognl:3.4.17`, and `mvn clean test` passes for all modules.

### Implementation for User Story 2

- [X] T013 [US2] Run `mvn clean test` from the repository root and confirm `mirage`, `miragesql-test` and `miragesql-integration` all match the T001 baseline (SC-001, SC-002)
- [X] T014 [P] [US2] Run `mvn dependency:tree -Dincludes=ognl:ognl` and confirm only `3.4.17` appears in every module (SC-003, FR-006); if an older copy is pulled transitively, add an exclusion or version management in the relevant `pom.xml`
- [X] T015 [P] [US2] If a dependency vulnerability scanner is available (e.g. `mvn org.owasp:dependency-check-maven:check`), run it and note OGNL findings in `specs/001-update-ognl/baseline.md`; otherwise record "not run" (SC-004). Done as "not run": advisory review recorded instead

**Checkpoint**: Single OGNL version across modules, full reactor green

---

## Phase 5: User Story 3 - Upgrade is documented for consumers (Priority: P3)

**Goal**: Consumers can see the old and new OGNL versions and any behavior differences.

**Independent Test**: The documented entry names 3.2.4 → 3.4.17 and lists differences (or states none).

### Implementation for User Story 3

- [X] T016 [US3] Add a short "Dependency updates" note to `README.md` stating OGNL 3.2.4 → 3.4.17 and either the contents of `specs/001-update-ognl/behavior-differences.md` or "no behavior differences found" (FR-008, SC-005; the repository has no changelog file)

**Checkpoint**: Upgrade documented

---

## Phase 6: Polish & Cross-Cutting Concerns

- [X] T017 Run the validation steps in `specs/001-update-ognl/quickstart.md` end to end and tick off each expected outcome
- [X] T018 Review `git diff` to confirm the change touches only the `mirage/pom.xml` bump, the minimum necessary source edits, `README.md` and spec artifacts (FR-007)

---

## Dependencies & Execution Order

- **Setup (Phase 1)** → **Foundational (Phase 2)** → **US1 (Phase 3)** → **US2 (Phase 4)** → **US3 (Phase 5)** → **Polish (Phase 6)**
- US2 and US3 depend on US1 (they validate and document the bumped dependency). US2 and US3 do not depend on each other.
- Within US1: T004 → T005 → (T006, T007, T008 in parallel, only where T005 reported errors) → T009 → (T010, T011) → T012.

### Parallel Opportunities

- T001 and T002 (T002 is read-only)
- T007 and T008 (different files)
- T014 and T015
- US2 and US3 can proceed in parallel after US1

## Parallel Example: User Story 1

```text
# After T005 identifies errors, in parallel:
Task: "Fix OGNL API usage in mirage/src/main/java/com/miragesql/miragesql/parser/SqlContextImpl.java"
Task: "Fix OGNL API usage in mirage/src/main/java/com/miragesql/miragesql/parser/SqlContextPropertyAccessor.java"
```

## Implementation Strategy

### MVP First (User Story 1 Only)

1. Phases 1-2: baseline and version confirmation
2. Phase 3: bump, compile, adapt, test
3. **Stop and validate**: `mvn -pl mirage -am test` equals baseline

### Incremental Delivery

1. MVP (US1) → 2. conflict-free dependency and full reactor (US2) → 3. documentation (US3) → polish

## Notes

- If T005 reports no errors, T006-T008 are marked N/A and no source files change.
- Do not select 3.5.0-BETA* releases.
- Commit after each phase.
