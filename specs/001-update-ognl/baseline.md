# Baseline (before OGNL bump)

Environment: JDK 25, Maven 3.9.6. The first measurements below were taken on a branch based on `main` (`java.version` 17, Spring Boot 3.2.0, version 3.0.0). They were repeated on the merged 3.0.1 base (`java.version` 21, Spring Boot 3.4.0) with identical results (see "Re-verified on 3.0.1 base" below). Run from a git worktree with `-Dmaven.gitcommitid.skip=true` (the old `git-commit-id-plugin` cannot read a worktree `.git`).

**Finding**: with the original POMs `mvn test` ran **0 tests** in every module. Surefire auto-selects the JUnit Platform provider (Spock is on the classpath) and there was no engine for the JUnit 4 tests. `junit-vintage-engine` (test scope) was added to the parent `pom.xml` so the baseline is meaningful.

## OGNL versions (T002)

`mvn dependency:tree -Dincludes=ognl:ognl`: `ognl:ognl:3.2.4` in `mirage`, `miragesql-test` and `miragesql-integration`.

## Test results on OGNL 3.2.4 (T001)

| Module | Tests run | Failures | Errors | Skipped |
|---|---|---|---|---|
| mirage | 46 | 0 | 0 | 0 |
| miragesql-test | 19 | 0 | 0 | 0 |
| miragesql-integration | 8 | 0 | 3 | 0 |

Pre-existing errors (unrelated to OGNL): `SpringConnectionProviderTest.testSpringSpringConnectionProvider1/2/3` fail with `CannotLoadBeanClass: org.apache.commons.dbcp.BasicDataSource` (class missing from the classpath).

## After the upgrade (OGNL 3.4.17)

| Module | Tests run | Failures | Errors | Skipped |
|---|---|---|---|---|
| mirage | 53 (46 existing + 7 new in `OgnlUtilTest`) | 0 | 0 | 0 |
| miragesql-test | 19 | 0 | 0 | 0 |
| miragesql-integration | 8 | 0 | 3 (same pre-existing `BasicDataSource` errors) | 0 |

- `mvn dependency:tree -Dincludes=ognl:ognl`: only `ognl:ognl:3.4.17` in all three modules.
- The 7 new tests also pass against the original code on OGNL 3.2.4, so they check behavior that is unchanged by the upgrade.
- Required source changes: `OgnlUtil.DefaultMemberAccess` (`setup`, `restore`, `isAccessible`) and `SqlContextPropertyAccessor.getProperty` take `OgnlContext` instead of `Map`. `SqlContextImpl` needed no change.

## Re-verified on 3.0.1 base (Java 21, Spring Boot 3.4.0)

After merging into `feature/upgrade-libs-3.0.2`: Maven and Eclipse both give `mirage` 53/53, `miragesql-test` 19/19 and `miragesql-integration` 5/8 (same 3 `BasicDataSource` errors). A run on JDK 17 with `-Djava.version=17` gave the same numbers, but the Java level was kept at 21 to match release 3.0.1.

## Fix of the pre-existing `BasicDataSource` errors

`miragesql-integration/src/test/resources/com/miragesql/miragesql/integration/spring/applicationContext.xml` referenced DBCP 1 (`org.apache.commons.dbcp.BasicDataSource`) while the project depends on `commons-dbcp2`. Changing the class to `org.apache.commons.dbcp2.BasicDataSource` makes `miragesql-integration` pass 8/8; the full reactor is now 53 + 19 + 8 tests, all passing.

## Vulnerability check (T015)

Scanner not run: the OWASP dependency-check plugin needs a large NVD download and was not attempted. Advisory review only: see `research.md` R6. CVE-2026-113730 affects `>=3.4.0 <3.4.13` per Mend and is fixed in 3.4.13, so 3.4.17 is not affected. This is from web sources and is not yet confirmed against NVD/GHSA.
