# Feature Specification: Upgrade to Latest Spring Boot 4

> **Update (maintenance/legacy branch)**: this document records the original `4.0.1` work and is kept as history. On the `maintenance/legacy` branch the artifacts are `miragesql-parent-legacy`, `miragesql-legacy`, `miragesql-integration-legacy` and `miragesql-test-legacy` under the group `vn.com.unit.miragesql`, the Java package stays `com.miragesql`, the minimum Java version is **17** (not 21), and the line is published to Maven Central. See `CHANGELOG.md` ("Release lines" and "Unreleased") for the current state.


**Feature Branch**: `002-upgrade-spring-boot-4`

**Created**: 2026-10-26

**Status**: Draft

**Input**: User description: "upgrade spring boot 4 mới nhất cho source này" (upgrade this source to the latest Spring Boot 4)

## User Scenarios & Testing *(mandatory)*

### User Story 1 - Project builds and passes all tests on the latest Spring Boot 4 (Priority: P1)

A maintainer moves the whole multi-module project (the core library, the framework-integration module and the test module) from Spring Boot 3.4.0 to the latest stable Spring Boot 4.x release. After the upgrade, the full build succeeds and every test that passed before still passes.

**Why this priority**: This is the core outcome of the request. Without a green build and test run on the new platform, nothing else has value.

**Independent Test**: Run a clean full build of all modules, including the complete test suite, and compare the results with the pre-upgrade baseline.

**Acceptance Scenarios**:

1. **Given** the project declares the latest stable Spring Boot 4.x release, **When** a clean build of all modules is run with tests, **Then** the build succeeds and 100% of the previously passing tests still pass.
2. **Given** the upgraded project, **When** the dependency tree is inspected, **Then** all Spring-managed libraries resolve to versions consistent with the Spring Boot 4 release, with no leftover Spring 6.x / Spring Boot 3.x artifacts.
3. **Given** the upgraded project, **When** it is compiled, **Then** no code uses removed or renamed Spring APIs, and any needed adaptation is limited to what the new version requires.

---

### User Story 2 - Spring integration of Mirage SQL keeps working for consumers (Priority: P2)

A developer who uses Mirage SQL with Spring (transaction management, data-source wiring, bean configuration, AOP-based integration, and the embedded web server dependency) upgrades their application to Spring Boot 4 together with the new Mirage release. Their existing Mirage SQL configuration and SQL-based data access behave exactly as before.

**Why this priority**: The Spring integration is the part of the library most directly affected by a Spring major upgrade; consumers depend on it keeping its behavior.

**Independent Test**: Run the integration tests that exercise Spring wiring, transactions and data access against a database, and confirm results match the pre-upgrade baseline.

**Acceptance Scenarios**:

1. **Given** an application context configured with Mirage SQL under Spring Boot 4, **When** it starts, **Then** all Mirage SQL beans are created and wired without errors or deprecation-removal failures.
2. **Given** a transactional data-access flow (insert, update, query, rollback), **When** it is run under Spring Boot 4, **Then** commit and rollback behavior and the SQL results are identical to the pre-upgrade behavior.
3. **Given** the library's public API and configuration conventions, **When** a consumer upgrades, **Then** no source change is required on the consumer side beyond the Spring Boot upgrade itself, or any required change is documented.

---

### User Story 3 - Release line, version and notes are clear for consumers (Priority: P3)

A consumer can tell which release line to use: `4.0.x` pairs with Spring Boot 4, while `3.0.x` stays available for Spring Boot 3. Both lines share the same group and artifact names, and the release notes state the Spring Boot version, the minimum Java version and any breaking changes or required actions.

**Why this priority**: Helps downstream users pick the right line and judge upgrade risk, but does not affect functionality.

**Independent Test**: Check that every module reports version `4.0.1`, that the notes contain the line-to-Spring-Boot pairing, and that a build of the `3.0.x` line still resolves Spring Boot 3.

**Acceptance Scenarios**:

1. **Given** the upgrade is complete, **When** a consumer reads the change notes, **Then** they find the previous and new Spring Boot versions, the minimum Java version, the pairing of `3.0.x` / `4.0.x` with Spring Boot generations, and any breaking changes.
2. **Given** the released `4.0.1` modules, **When** their project versions are inspected, **Then** all report `4.0.1` with the same group and artifact names as `3.0.1`.
3. **Given** the Spring Boot 3 line, **When** it is built after `4.0.1` is released, **Then** it still resolves Spring Boot 3 and no version number is shared between the two lines.

---

### Edge Cases

- The newest published Spring Boot 4 version is a milestone or release candidate: it is not selected; the latest stable 4.x release is used.
- Spring Boot 4 splits or renames modules and starters that Spring Boot 3 provided as one: affected dependencies are mapped to their new equivalents so that no functionality is lost.
- Third-party libraries managed by the Spring Boot dependency management (for example the JUnit, logging, database connection-pool and database-driver libraries) change versions: tests that relied on old behavior are identified and the differences are recorded rather than silently altered.
- Libraries pinned explicitly by the project (for example OGNL, already updated in a prior feature) are incompatible with the new platform: the incompatibility is identified and resolved without reverting previously delivered upgrades.
- Spring APIs that were deprecated in 3.x and removed in the new major version are still used by the integration code: they are replaced with their supported equivalents while preserving behavior.
- The new Spring Boot version requires a different minimum Java version than the project's current baseline (Java 17 on `maintenance/legacy`, originally Java 21): the baseline is kept if compatible, otherwise the required change is stated explicitly.
- A consumer on Spring Boot 3 accidentally upgrades to `4.0.1`: the change notes and the version-line pairing make the mismatch easy to diagnose, and the `3.0.x` line stays available for them.
- The separately versioned companion Spring Data module (currently 3.0.1) may not be compatible with the new platform: this must be surfaced and its handling decided (see Assumptions).

## Requirements *(mandatory)*

### Functional Requirements

- **FR-001**: The project MUST use the latest stable (non-milestone, non-RC) Spring Boot 4.x release available at the time of implementation, replacing the current 3.4.0, across all modules.
- **FR-002**: All modules (core library, integration module, test module) MUST build successfully from a clean state after the upgrade.
- **FR-003**: The complete existing automated test suite MUST pass after the upgrade, with no change to expected SQL output or data-access results.
- **FR-004**: Spring-related dependencies declared by the project MUST be aligned with Spring Boot 4 and its managed Spring Framework version, with no mixed major versions in the resolved dependency set.
- **FR-005**: Existing Spring integration behavior of Mirage SQL (bean wiring, transaction handling, data-source usage, AOP integration) MUST be preserved.
- **FR-006**: Any source or configuration change needed to adapt to removed, renamed or relocated Spring APIs MUST be limited to what is needed to preserve existing behavior; no unrelated refactoring.
- **FR-007**: The project's required Java version MUST remain satisfied by Spring Boot 4; if the new platform requires a different Java baseline, this MUST be stated explicitly in the change notes.
- **FR-008**: Previously delivered upgrades (Java 17 baseline, originally 21, OGNL update, JUnit Platform with the vintage engine) MUST continue to work after this upgrade.
- **FR-009**: Any behavior difference or required consumer action resulting from the upgrade MUST be recorded in the change notes, together with the old and new Spring Boot versions.
- **FR-010**: The upgraded dependency set MUST NOT introduce a known vulnerability that is fixed in a later stable release.
- **FR-011**: The project version of all modules MUST be `4.0.1` for the Spring Boot 4 release, with the same group and artifact names as today so consumers choose the line by version number only.
- **FR-012**: The existing Spring Boot 3 line MUST remain maintainable independently: its versions stay `3.0.x` (next is `3.0.2`), and releasing `4.0.1` MUST NOT change or overwrite any published `3.0.x` version. Creating its long-lived maintenance branch (`support/3.x`) is a maintainer step at release time.
- **FR-013**: The change notes MUST state which version line pairs with which Spring Boot generation (`3.0.x` with Spring Boot 3.4, `4.0.x` with Spring Boot 4.1) so consumers do not pick the wrong line.
- **FR-014**: Branch and tag names for both lines MUST follow one convention: `release/<version>` for a release, `support/3.x` for maintenance of the Spring Boot 3 line, `feature/spring-boot-4` for the upgrade work, and tag `4.0.1` for the release. The implementation creates only the local `feature/spring-boot-4` branch; the remaining branches and the tag are created by a maintainer, following the checklist in the release steps.

### Key Entities

- **Spring Boot platform version**: The version that governs Spring Framework and managed third-party library versions for all modules; attributes: current version (3.4.0), target version (latest stable 4.x), release status.
- **Spring-integration module**: The part of the project that wires Mirage SQL into Spring (transactions, data sources, AOP, bean configuration) and must keep its behavior.
- **Companion Spring Data module**: The separately released module version referenced by the project, which depends on the platform version and may need its own compatible release.
- **Release line**: A series of releases that pairs a project major version with a Spring Boot generation; attributes: version series (`3.0.x` or `4.0.x`), paired Spring Boot generation, maintenance branch, status (maintenance or active development).
- **Change notes**: The consumer-facing record of versions, requirements and breaking changes.

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: 100% of the tests that passed before the upgrade still pass after it, with zero changes to expected SQL output.
- **SC-002**: A clean build of all 3 modules succeeds in a single run on the supported Java version.
- **SC-003**: The resolved dependency tree contains 0 artifacts from the Spring Boot 3.x / Spring Framework 6.x generation, and the Spring Boot version used equals the latest stable 4.x release.
- **SC-004**: The Spring-integrated data-access scenarios (wiring, query, insert/update, commit, rollback) produce results identical to the pre-upgrade baseline in 100% of cases.
- **SC-005**: Change notes list the old and new Spring Boot versions and the minimum Java requirement for 100% of upgrade deliveries.
- **SC-006**: A standard dependency vulnerability scan, or a review of public advisories (NVD/GHSA) if no scanner is available, reports no known vulnerability in the chosen versions that is fixed in a later stable release.
- **SC-007**: After release, both lines can be built and published independently: a build of the `3.0.x` line still resolves Spring Boot 3, a build of `4.0.1` resolves Spring Boot 4, and no version number is used by both.

## Assumptions

- "Latest Spring Boot 4" means the latest stable 4.x release; milestones and release candidates are excluded. The exact version is to be confirmed against the official release sources during planning.
- The Spring Boot version is declared once in the parent project and inherited by all modules, with Spring libraries in the integration module following that version.
- The Java baseline (17 on `maintenance/legacy`, originally 21 in release 3.0.1) is expected to remain valid for Spring Boot 4; this is to be verified during planning.
- The existing automated test suite is an adequate regression safety net; new tests are added only if a gap is found during the upgrade.
- The companion Spring Data module version (3.0.1.RELEASE) is a property of this repository's release management; whether it needs a new release for Spring Boot 4 compatibility is to be determined during planning and, if it is a separate repository, is out of scope.
- Upgrading unrelated libraries that are not managed by Spring Boot is out of scope, except where required to remain compatible with Spring Boot 4.
- The `4.0.1` release requires Spring Boot 4; consumers who stay on Spring Boot 3 keep using the `3.0.x` line, which is maintained separately (FR-012) and is not changed by this feature beyond its own already planned `3.0.2` work.
- The project is a fork (repository `miragesql-customize` on the internal Git server) of the original open-source Mirage SQL; group and artifact names stay as they are and only the version number tells the lines apart. Renaming the group or packages is out of scope.
- Creating or renaming git branches, and publishing to the internal Maven repository, are release-management steps performed by a maintainer at delivery time; this spec only defines the target names and versions.
