# Quickstart: Validate the OGNL Upgrade

**Prerequisites**: JDK 17+ (the build targets Java 17), Maven, network access to Maven Central. Run from the repository root.

1. **Baseline** (before the bump, to know pre-existing failures):
   `mvn -q test` and record the pass/fail counts per module.
2. **Apply the bump**: set the `ognl` version in `mirage/pom.xml` to `3.4.17`.
3. **Compile and unit tests**: `mvn -pl mirage -am test`. Expected: compiles with no new errors; test results equal the baseline (SC-001).
4. **Full reactor**: `mvn clean test`. Expected: `mirage`, `miragesql-test` and `miragesql-integration` all build and pass as in the baseline (SC-002, FR-005).
5. **Single version check**: `mvn dependency:tree -Dincludes=ognl:ognl`. Expected: only `ognl:ognl:3.4.17` in each module that shows it (SC-003, FR-006).
6. **Error reporting check**: confirm an existing test (or add one if missing) with an invalid expression yields `OgnlRuntimeException` including path and line number (FR-003).
7. **Non-public access check**: confirm a template evaluating a private/protected member still resolves, as it did before (FR-004).
8. **Notes**: add old and new versions, plus any unavoidable behavior differences, to the change notes (FR-008, SC-005).

See [research.md](research.md) for the expected API risk areas to watch for at step 3.
