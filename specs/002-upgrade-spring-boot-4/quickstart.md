# Quickstart: Validate the Spring Boot 4 Upgrade

> **Update (maintenance/legacy branch)**: this document records the original `4.0.1` work and is kept as history. On the `maintenance/legacy` branch the artifacts are `miragesql-parent-legacy`, `miragesql-legacy`, `miragesql-integration-legacy` and `miragesql-test-legacy` under the group `vn.com.unit.miragesql`, the Java package stays `com.miragesql`, the minimum Java version is **17** (not 21), and the line is published to Maven Central. See `CHANGELOG.md` ("Release lines" and "Unreleased") for the current state.


## Prerequisites

- JDK 17 or newer and Maven 3.9.x on the path (`mvn -v` shows Java 17+).
- Network access to Maven Central (the `repository` directory in the repo is also configured as a local repository).

## 1. Capture the baseline (before changing anything)

From the repo root on the pre-upgrade commit:

```text
mvn clean test
```

Record the per-module test counts (passed, failed, skipped). This is the baseline for SC-001.

## 2. Apply the upgrade

Set `spring.boot.version` to `4.1.2` in the root `pom.xml`, then fix only what the build or tests report (see research R5).

## 3. Clean build with tests

```text
mvn clean test
```

Expected: all 3 modules build; counts match the baseline (SC-001, SC-002). Also run `mvn clean verify` to include packaging.

## 4. Spring integration scenarios

Covered by the existing integration tests, in particular `SpringConnectionProviderTest` and `TransactionInterceptorTest` in `miragesql-integration`. Expected: wiring, query, insert/update, commit and rollback results match the baseline (SC-004).

## 5. Dependency tree

```text
mvn dependency:tree -Dincludes=org.springframework,org.springframework.boot
```

Expected: every Spring artifact is on 7.0.x / 4.1.2; none on 6.x / 3.x (SC-003).

## 6. Vulnerability check

Run the OWASP dependency-check plugin if it works with this build, otherwise review GHSA/NVD for the resolved Spring versions, and record the outcome (SC-006).

## 7. Version and line check

- `mvn help:evaluate -Dexpression=project.version -q -DforceStdout` prints `4.0.1` in each module, and `mirage.version` resolves to the same value.
- The built artifacts are named `miragesql-legacy-4.0.1`, `miragesql-integration-legacy-4.0.1` and `miragesql-test-legacy-4.0.1` (group `vn.com.unit.miragesql`); the original 3.0.1 artifacts had no `-legacy` suffix and used the group `com.miragesql`.
- On the `3.0.x` line (`support/3.x`), the same dependency-tree command still shows Spring Boot 3 artifacts (SC-007).

## 8. Change notes

Confirm the change notes state the old and new Spring Boot versions, that Java 17 is the baseline, which version line pairs with which Spring Boot generation, and any consumer action (SC-005, FR-013).
