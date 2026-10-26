# Baseline (before the Spring Boot 4 upgrade)

Recorded 2026-10-26 on branch `feature/upgrade-libs-3.0.2` (commit 8d85fdf), JDK 21.0.1, Maven 3.9.6, project version 3.0.1, Spring Boot 3.4.0.

## T001: `mvn clean test`

BUILD SUCCESS, no pre-existing failures.

| Module | Tests run | Failures | Errors | Skipped |
|--------|-----------|----------|--------|---------|
| mirage (Mirage-SQL) | 53 | 0 | 0 | 0 |
| miragesql-test | 19 | 0 | 0 | 0 |
| miragesql-integration | 8 | 0 | 0 | 0 |
| **Total** | **80** | 0 | 0 | 0 |

## T002: resolved versions (Spring, JUnit Platform, Spock, Groovy, OGNL)

| Item | Version |
|------|---------|
| spring-boot-starter-tomcat (mirage and transitively others) | 3.4.0 |
| spring-core / context / jdbc / aop (miragesql-integration) | 6.2.0 |
| junit-vintage-engine | 5.11.3 |
| junit-platform-engine / commons / launcher | 1.11.3 |
| spock-core in mirage | 2.4-M1-groovy-4.0 |
| spock-core in miragesql-test | 1.1-groovy-2.4 (resolved version, differs from mirage) |
| groovy-all (pom) / groovy modules | 4.0.17 / 4.0.24 |
| ognl | 3.4.17 (single version in every module) |

Observation: the Boot 3.4.0 managed versions in `research.md` R3 are approximate; the values in this table are the real ones for the entries it covers.

## T025: result after the upgrade (branch `feature/spring-boot-4`, version 4.0.1, Spring Boot 4.1.2)

| Criterion | Result |
|-----------|--------|
| SC-001 tests unchanged | Met: 53 / 19 / 8 = 80 tests pass, same as baseline, no test or expected-output change |
| SC-002 clean build of 3 modules | Met: `mvn clean verify` BUILD SUCCESS on JDK 21 |
| SC-003 no Spring 6.x / Boot 3.x | Met: all Spring artifacts are 7.0.10 / 4.1.2 |
| SC-004 Spring data-access identical | Met by the existing tests (`SpringConnectionProviderTest`, `TransactionInterceptorTest`); no new tests added |
| SC-005 change notes | Met: `CHANGELOG.md` |
| SC-006 vulnerability review | Partly met: GHSA spot check on 5 key packages found nothing; full scan not run (see `research.md`) |
| SC-007 two lines independent | Met locally: `release/3.0.1` still resolves Boot 3.4.0; nothing published |

Caveat: the Spock specs in `mirage/src/test/groovy` are not executed by this build, before or after the upgrade (see `research.md` "Test tooling"), so "all tests pass" means the 80 tests that run.
