# Research: Upgrade to Latest Spring Boot 4

> **Update (maintenance/legacy branch)**: this document records the original `4.0.1` work and is kept as history. On the `maintenance/legacy` branch the artifacts are `miragesql-parent-legacy`, `miragesql-legacy`, `miragesql-integration-legacy` and `miragesql-test-legacy` under the group `vn.com.unit.miragesql`, the Java package stays `com.miragesql`, the minimum Java version is **17** (not 21), and the line is published to Maven Central. See `CHANGELOG.md` ("Release lines" and "Unreleased") for the current state.


Checked 2026-10-26 against Maven Central metadata and POMs (`repo.maven.apache.org`).

## R1: Which version is "latest Spring Boot 4"?

- **Decision**: 4.1.2.
- **Rationale**: `spring-boot-starter-parent/maven-metadata.xml` lists 4.0.x up to 4.0.9, then 4.1.0-M1..RC1, 4.1.0, 4.1.1, 4.1.2, and 4.2.0-M1/M2/RC1. 4.1.2 is the newest non-milestone, non-RC entry. 4.2.0 has no GA yet.
- **Alternatives considered**: 4.2.0-RC1 (rejected: pre-release, excluded by spec); 4.0.9 (rejected: older line, does not meet "latest").

## R2: Java baseline

- **Decision**: Keep `java.version` = 21. (Superseded: the legacy branch now sets `java.version` = 17, matching the Spring Boot 4.1.2 minimum.)
- **Rationale**: The 4.1.2 starter parent sets `java.version` to 17, so Java 17 is the minimum and Java 21 was satisfied in the original work. The build JDK in this environment is 21.0.1 (Maven 3.9.6); a JDK 25 is also installed but not used by Maven.
- **Alternatives considered**: Lowering to 17 (rejected: contradicts release 3.0.1 baseline).

## R3: What does Boot 4.1.2 manage that this project uses?

From `spring-boot-dependencies-4.1.2.pom`:

| Library | Currently (Boot 3.4.0 managed) | Boot 4.1.2 managed |
|---------|-------------------------------|--------------------|
| Spring Framework | 6.2.x | 7.0.10 |
| JUnit Jupiter / Platform | 5.11.x | 6.0.3 |
| JUnit 4 | 4.13.2 | 4.13.2 |
| Mockito | 5.14.x | 5.23.0 |
| HikariCP | 5.1.x | 7.0.2 |
| commons-dbcp2 | 2.12.x | 2.14.0 (module pins 2.11.0 explicitly) |
| H2 / HSQLDB | 2.3.x / 2.7.x | 2.4.240 / 2.7.3 |
| Tomcat | 10.1.x | 11.0.27 |
| Jakarta Servlet / Transaction | 6.0 / 2.0.1 | 6.1.0 / 2.0.1 |
| Groovy (`org.apache.groovy`) | 4.0.x | 5.0.8 |
| Lombok / SLF4J | - | 1.18.48 / 2.0.20 |

"Currently" column values are approximate (not re-read from the 3.4.0 BOM); the point is the direction of each change, to be confirmed by the first build.

## R4: Where does the project touch Spring or Boot-managed APIs?

Verified by searching the repository:

- `mirage/pom.xml`: depends on `spring-boot-starter-tomcat` only to get the Jakarta Servlet API for `OpenSessionInViewFilter` and `SchemaUpdateListener` (the only `jakarta.servlet` users).
- `miragesql-integration`: `spring-core`, `spring-context`, `spring-jdbc` (optional) and `spring-aop` (test). Main code uses only `SpringConnectionProvider`, which imports `ConnectionHolder`, `DataSourceTransactionManager`, `TransactionAwareDataSourceProxy` and `TransactionSynchronizationManager`. Tests use `ClassPathXmlApplicationContext`, `@Transactional` and an XML context that references the 2.5 schema locations.
- `SeasarConnectionProvider` uses `jakarta.transaction`.
- Root POM: Boot is imported as `spring-boot-starter-parent` in `dependencyManagement` (type pom, scope import) and as a pinned `spring-boot-maven-plugin` version; the version is declared once (`spring.boot.version`).
- `spring-data-mirage.version` (3.0.1.RELEASE) is a property in the root POM that no module references. It is not a dependency of this build.

## R5: Likely risks (to be confirmed by building and running tests)

These come from the changed versions above, not from a full compatibility audit; the first implementation step is to bump, build and test to see which actually occur.

1. **Spock vs JUnit Platform 6**: the module pins Spock `2.4-M1-groovy-4.0` and Groovy 4.0.17 while Boot 4 brings JUnit Platform 6. Newer Spock builds exist (`2.4-groovy-4.0`, final). Decision if the Spock tests fail to load: move to `2.4-groovy-4.0`, keep Groovy 4.
2. **junit-vintage-engine** is managed by Boot; 6.1.x exists on Maven Central, so the JUnit 4 tests should keep running through the vintage engine.
3. **Spring Framework 7**: the XML test context uses the 2.5 schema locations. Spring 7's `spring.schemas` still maps `spring-beans-2.5.xsd` to the current schema, so this should resolve; `spring-aop` / `spring-tx` equivalents are checked when the test runs.
4. **Servlet API supplier**: `spring-boot-starter-tomcat` still exists in 4.1.2 (it now pulls `spring-boot-tomcat` and `spring-boot-starter-tomcat-runtime`). Keeping it satisfies FR-006; replacing it with `jakarta.servlet-api` directly would be a separate cleanup.
5. **HikariCP 7 / H2 2.4**: managed versions jump; the DB-backed tests are the oracle.
6. **Explicit pins** (`javassist` 3.29.2-GA, `ognl` 3.4.17, `guice` 5.0.1, `cglib` 3.3.0, `commons-dbcp2` 2.11.0) stay as-is unless the build or tests show an incompatibility (FR-006/FR-008 in the spec).

- **Decision**: Change only `spring.boot.version`, build, and fix what the build and tests actually report. Use the existing suite as the oracle.
- **Alternatives considered**: Pre-emptively removing the Tomcat starter or bumping all pinned libraries (rejected: violates the minimal-change rule, FR-006).

## R6: Companion module `spring-data-mirage`

- **Decision**: No action in this repository.
- **Rationale**: R4 shows nothing references `spring-data-mirage.version`. If the separate Spring Data module needs a Boot 4 release, that is tracked in its own repository (out of scope per spec Assumptions).

## R7: Vulnerabilities

- **Decision**: Checked after the upgrade by reviewing GitHub Security Advisories (GHSA); result in "Upgrade findings" below. The OWASP plugin was not run.
- **Rationale**: The OWASP dependency-check plugin is configured in the parent POM but bound to `phase none` (7.4.1, which is old). Verification is done after the upgrade by running the plugin if it works, otherwise by reviewing GHSA/NVD for Spring Boot 4.1.2 / Spring Framework 7.0.10 and recording the result (spec SC-006).

## Upgrade findings

Recorded during implementation (2026-10-26) on branch `feature/spring-boot-4`, JDK 21.0.1.

### US1: Spring Boot 3.4.0 to 4.1.2

- Only `spring.boot.version` in the root `pom.xml` was changed. `mvn clean test-compile` and `mvn clean test` both succeed with no POM mapping and no Java source change (T006, T008 needed no edits).
- Test counts match `baseline.md`: mirage 53, miragesql-test 19, miragesql-integration 8, all passing, 0 skipped.
- Dependency tree: every Spring artifact is on Spring Framework 7.0.10 (`spring-core`, `spring-context`, `spring-beans`, `spring-aop`, `spring-expression`, `spring-jdbc`, `spring-tx`) and Spring Boot 4.1.2; none on 6.x or Boot 3.x (T013).
- `spring-boot-starter-tomcat` 4.1.2 still resolves; it now brings `spring-boot-starter`, `spring-boot-tomcat`, `spring-boot-starter-tomcat-runtime`, `spring-boot-web-server` and the Spring libraries listed above transitively into `mirage`. This is a larger footprint than the Boot 3 starter had (Boot 3.4.0 listed no Spring artifacts under it in the baseline tree); consider replacing it with `jakarta.servlet-api` as a separate change (out of scope here, R5 item 4).
- JUnit Platform moved 1.11.3 to 6.0.3 and `junit-vintage-engine` 5.11.3 to 6.0.3; JUnit 4 tests are still discovered and run (R5 item 2 resolved: no change needed).

### Pinned libraries (T007)

`ognl` 3.4.17 (single version in every module), `javassist` 3.29.2-GA, `commons-dbcp2` 2.11.0, `guice` 5.0.1 and `cglib` 3.3.0 were kept. No conflict showed up in the build or in the 80 tests, so no pin was changed.

### Test tooling (R5 item 1)

The Spock specs under `mirage/src/test/groovy` are **not executed** in this build, before or after the upgrade: there is no Groovy compile plugin in the POMs and `mirage/target/surefire-reports` contains no `*Spec` report. The test counts above therefore do not include them. This is a pre-existing condition, not caused by the upgrade, so no Spock version change was made (T009). Making the specs run would be a separate change.

Also pre-existing: `miragesql-test` resolves `spock-core` 1.1-groovy-2.4 while `mirage` declares 2.4-M1-groovy-4.0.

### Managed-library jumps (T011)

No test failed after the upgrade, so no code or test adaptation and no extra pin was needed.

### US2: Spring integration

- `SpringConnectionProviderTest` (3 tests) and `TransactionInterceptorTest` (2 tests) pass on Spring Framework 7.0.10 with results identical to the baseline; the test XML context (schema locations `spring-beans-2.5.xsd`, `spring-tx-2.5.xsd`, `spring-aop-2.5.xsd`) still resolves without edits (T014).
- `git diff feature/upgrade-libs-3.0.2 -- mirage/src miragesql-test/src miragesql-integration/src` is empty: no public class or method signature changed, so no consumer-side source change is needed beyond moving to Spring Boot 4 (T015).
- Coverage gap check: the Spring tests cover wiring, queries and the transaction-aware connection handling used by `SpringConnectionProvider`; no gap was identified that requires a new test (T016, N/A).

### US3: version, notes, vulnerability review, release line

- Version: root `pom.xml` and the `parent` block of the three module POMs were set to `4.0.1` (T017, T018). `mvn clean verify` succeeds for all modules with the same 80 tests passing, and builds `miragesql-4.0.1.jar`, `miragesql-test-4.0.1.jar` and `miragesql-integration-4.0.1.jar` (T019). `spring-data-mirage.version` (3.0.1.RELEASE) is a separate unused property and was left unchanged.
- Change notes: `CHANGELOG.md` created at the repository root with the line-to-Spring-Boot table and the 4.0.1 entry (T020). `README.md` has no version or Spring references, so nothing to update there.
- Vulnerability review (T021, SC-006), 2026-10-26: queried the GitHub advisory database (`api.github.com/advisories?ecosystem=maven&affects=<package>@<version>`) for `spring-core` 7.0.10, `spring-boot` 4.1.2, `tomcat-embed-core` 11.0.27, `HikariCP` 7.0.2 and `h2` 2.4.240: no advisory returned for any of them. A sanity query for `spring-core` 6.0.0 did return an advisory, so the query itself works. Limits: GHSA only, not NVD, and not the full dependency set; the OWASP dependency-check plugin was not run (pinned version 7.4.1 and its suppression file `security/project-suppression.xml` does not exist in the repository). Treat this as a spot check, not a full scan.
- Release line check (T022, SC-007): a temporary detached worktree of `release/3.0.1` resolves project version 3.0.1 with Spring Boot 3.4.0 and Spring Framework 6.2.0, so the Boot 3 line is unaffected. The worktree was removed afterwards. Note: a worktree path under the scratchpad directory failed with "Filename too long" on Windows; a short path (`C:/wt301`) worked.
