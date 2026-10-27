# Feature Specification: Update OGNL Library to Latest Release

> **Update (maintenance/legacy branch)**: this document records the OGNL update and is kept as history. The minimum Java version is now **17** (not 21) and the artifacts are now named with a `-legacy` suffix under the group `vn.com.unit.miragesql`. See `CHANGELOG.md`.


**Feature Branch**: `001-update-ognl`

**Created**: 2026-10-26

**Status**: Draft

**Input**: User description: "update library ognl từ bản hiện tại lên mới nhất" (update the OGNL library from the current version to the latest)

## User Scenarios & Testing *(mandatory)*

### User Story 1 - Library runs on the latest stable OGNL without behavior change (Priority: P1)

A developer using Mirage SQL upgrades to the new Mirage release. The expression-evaluation dependency (OGNL, currently 3.2.4) has been moved to the latest stable release. All existing 2-way SQL templates that use conditions (`/*IF ...*/`), bind variables and parenthesized bind variables evaluate to exactly the same results as before.

**Why this priority**: The upgrade is only acceptable if it does not break existing SQL templates; this is the core value and the minimum viable outcome.

**Independent Test**: Run the full existing test suite against the upgraded dependency and confirm it passes with no test changes that alter expected SQL output.

**Acceptance Scenarios**:

1. **Given** the project declares the latest stable OGNL version, **When** the project is built and the existing test suite is run, **Then** the build succeeds and all previously passing tests still pass.
2. **Given** a SQL template using IF conditions, bind variables and property access on beans, maps and entities, **When** it is evaluated, **Then** the generated SQL and bound parameters are identical to those produced before the upgrade.
3. **Given** a SQL template containing an invalid expression, **When** it is evaluated, **Then** the user receives the same kind of error (an expression error with file path and line number) as before.

---

### User Story 2 - Dependency is verifiably current and conflict-free (Priority: P2)

A maintainer inspecting the project's dependencies sees the latest stable OGNL version declared once, with no older OGNL copy pulled in transitively, so the artifact shipped to users contains exactly one OGNL version.

**Why this priority**: Prevents hidden version conflicts at runtime for downstream users.

**Independent Test**: Inspect the resolved dependency tree and confirm a single OGNL version equal to the target.

**Acceptance Scenarios**:

1. **Given** the upgraded project, **When** the dependency tree is listed, **Then** exactly one OGNL version appears and it matches the declared latest stable version.
2. **Given** the project is used together with the sibling modules in this repository, **When** all modules are built, **Then** all modules build successfully.

---

### User Story 3 - Upgrade is documented for consumers (Priority: P3)

A consumer reading the release notes can see which OGNL version is now used and whether any action is required on their side.

**Why this priority**: Helps downstream users judge upgrade risk, but does not affect functionality.

**Independent Test**: Review release notes/changelog for an entry stating the old and new OGNL versions.

**Acceptance Scenarios**:

1. **Given** the upgrade is complete, **When** a consumer reads the change notes, **Then** they find the previous and new OGNL versions and any behavior differences.

---

### Edge Cases

- The newest published version is a pre-release (alpha/beta/RC): it is not selected; the latest stable release is used instead.
- The new OGNL version changes or removes a programmatic interface that Mirage SQL's expression evaluation relies on (for example, member-access control or context construction): the affected code is adapted so that behavior is preserved, including access to private/protected/package members that templates may currently use.
- The new OGNL version is stricter about expression security (restricted methods or classes): any template in the existing tests that relied on previously permitted behavior is identified and the difference is documented rather than silently changed.
- The new version requires a newer minimum Java runtime than the project targets: the project's Java baseline (currently Java 17; originally 21) must remain satisfied.
- Custom property-access handling for SQL contexts (maps/entities with special access rules) must continue to resolve properties identically.

## Requirements *(mandatory)*

### Functional Requirements

- **FR-001**: The project MUST depend on the latest stable (non-pre-release) published version of OGNL at the time of implementation, replacing the current 3.2.4.
- **FR-002**: All existing SQL template features that evaluate expressions (IF conditions, bind variables, parenthesized bind variables, property access via custom accessors) MUST produce results identical to the pre-upgrade behavior.
- **FR-003**: Expression errors MUST continue to be reported with the original error type, including template path and line number where available.
- **FR-004**: Expression parsing and evaluation MUST continue to allow access to non-public members of objects where it is allowed today.
- **FR-005**: The project MUST build and pass its complete existing test suite on its supported Java baseline after the upgrade.
- **FR-006**: The resolved dependency set MUST contain a single OGNL version, with no conflicting older copy.
- **FR-007**: Any source change required to adapt to the new OGNL version MUST be limited to what is needed to preserve existing behavior; no unrelated refactoring.
- **FR-008**: Any behavior difference introduced by the new OGNL version that cannot be avoided MUST be recorded in the change notes.

### Key Entities

- **OGNL dependency**: The expression-language library used to evaluate conditions and parameters in SQL templates; attributes: current version (3.2.4), target version (latest stable), release status (stable vs pre-release).
- **SQL template expression**: An expression embedded in a 2-way SQL template whose evaluation result must stay unchanged by the upgrade.

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: 100% of the tests that passed before the upgrade still pass after it, with zero changes to expected SQL output.
- **SC-002**: The project builds successfully from a clean state on the supported Java version in a single run.
- **SC-003**: The resolved dependency tree shows exactly 1 version of OGNL, equal to the latest stable release.
- **SC-004**: The OGNL version in use has no known vulnerability that the previous version (3.2.4) did not already have, and none that is fixed in a later stable release, as reported by a standard dependency vulnerability scan or the public advisories (NVD/GHSA). If no scanner is available, the check is done by reviewing those advisories and the result is recorded.
- **SC-005**: Release notes list the old and new OGNL versions for 100% of upgrade deliveries.

## Assumptions

- "Latest" means the latest stable release; pre-release versions (alpha, beta, RC) are excluded. At the time of writing, a search shows a 3.5.0-BETA line on Maven Central; the actual newest stable version is to be confirmed against Maven Central during planning.
- The OGNL dependency is declared only in the `mirage` module's POM and used in a small set of classes (expression utility, IF node, bind-variable nodes, SQL context and its property accessor).
- The project's Java baseline (Java 17 on `maintenance/legacy`; 21 in release 3.0.1) and Spring Boot 3.4.0 stay unchanged by this feature.
- The existing automated test suite is an adequate regression safety net for expression behavior; new tests are added only if a gap is found during the upgrade.
- Upgrading other libraries is out of scope.
