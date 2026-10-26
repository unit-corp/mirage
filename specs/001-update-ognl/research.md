# Research: Update OGNL Library

## R1: Which version is "latest"?

- **Decision**: 3.4.17 (latest stable).
- **Rationale**: Maven Central metadata (`repo1.maven.org/maven2/ognl/ognl/maven-metadata.xml`, lastUpdated 2026-10-23) lists `latest` and `release` as 3.5.0-BETA10, but the spec excludes pre-releases. 3.4.17 is the newest non-beta entry.
- **Alternatives considered**: 3.5.0-BETA10 (rejected: pre-release, API still moving); staying on 3.2.x line (rejected: does not meet the request).

## R2: Which code touches the OGNL API?

Verified by searching the repository:

- `OgnlUtil.getValue(...)`: builds `new OgnlContext(null, null, new DefaultMemberAccess(true))` and calls `Ognl.getValue(exp, context, root)`; `parseExpression` calls `Ognl.parseExpression`. Also defines an inner `DefaultMemberAccess implements ognl.MemberAccess` (uses `AccessibleObject.isAccessible()`, deprecated since Java 9).
- `SqlContextImpl`: `OgnlRuntime.setPropertyAccessor(SqlContext.class, ...)`.
- `SqlContextPropertyAccessor`: extends `ObjectPropertyAccessor`, overrides a method that `throws OgnlException`.
- `IfNode`, `ParenBindVariableNode`: only call `OgnlUtil`, no direct OGNL types.
- `OgnlRuntimeException`: references OGNL only in name/Javadoc (3 grep hits).
- OGNL is declared only in `mirage/pom.xml`; the other two modules do not declare it.

Conclusion: the surface is small and concentrated in `OgnlUtil`, `SqlContextImpl` and `SqlContextPropertyAccessor`.

## R3: Likely 3.2.4 → 3.4.x compatibility risks (to be confirmed by compiling and running tests)

These come from general knowledge of the OGNL 3.3/3.4 line and have **not** been verified against 3.4.17 sources yet; the first implementation step is a compile and test run to see which actually occur.

- The `OgnlContext` constructor taking `(ClassResolver, TypeConverter, MemberAccess)` may be deprecated or removed in favor of a `MemberAccess`-first constructor.
- `MemberAccess` interface and `OgnlRuntime.setPropertyAccessor` may be deprecated or have changed generics/signatures.
- 3.3.x/3.4.x tightened expression security (blocked classes/methods, static-method access restrictions). Templates that call static methods or restricted classes could behave differently.
- Javadoc/behavior of `ObjectPropertyAccessor` methods overridden by `SqlContextPropertyAccessor` may have changed.

- **Decision**: Bump the version first, compile, and fix only the errors/warnings that matter for behavior; use the existing test suite as the oracle. If a restriction breaks a legitimate template, document it (FR-008) instead of disabling OGNL security globally.
- **Alternatives considered**: Pre-emptively rewriting `DefaultMemberAccess` (rejected: violates minimal-diff, FR-007).

## R4: Java baseline

OGNL 3.4.x targets Java 8+, so the project's Java 21 (`java.version`, release 3.0.1 and branch `feature/upgrade-libs-3.0.2`) is satisfied. Java 17 was only the baseline of `main` (3.0.0); it also works (verified with JDK 17), but the Java level stays at 21 to match 3.0.1.

## R6: Known vulnerabilities (checked 2026-10-26)

Source: web search only; **not yet confirmed against NVD/GHSA** (confirm before relying on it).

- **CVE-2026-113730** (CVSS v3 5.6, published 2026-10-20): in the optional stricter-invocation mode, `OgnlRuntime.invokeMethod` checks only the declaring class and does not block `java.lang.reflect.Method`, `Constructor` or `Field`. Affected range per Mend: `>=3.4.0 <3.4.13`; fixed in 3.4.13. The identifier has an unusual 6-digit sequence, so verify it. Sources: [Mend](https://www.mend.io/vulnerability-database/CVE-2026-113730/), [Vulert](https://vulert.com/vuln-db/CVE-2026-113730).
  - 3.4.17 (target) is outside the range, so it is fixed.
  - 3.2.4 (current) is outside the range. Whether 3.3.x is affected was not confirmed.
  - Snyk did not list this CVE at the time of search (its data may lag).
- **CVE-2016-3093** (cache-poisoning DoS): fixed in 3.0.12; affects neither 3.2.4 nor 3.4.17.
- **Not applicable**: CVE-2025-53192 concerns Apache Commons OGNL, a different project and artifact; Confluence CVEs (e.g. CVE-2021-26084) are injection flaws in that product, not in the `ognl:ognl` library.

- **Decision**: Target 3.4.17 satisfies SC-004 on current information; T015 still runs a scanner (or advisory review) to record the result.
- **Alternatives considered**: Any 3.4.x below 3.4.13 (rejected: affected by CVE-2026-113730).

## R5: Verification approach

Run `mvn -pl mirage -am test`, then the full reactor build, then `mvn dependency:tree -Dincludes=ognl` to confirm a single OGNL version (SC-003). A vulnerability scan (SC-004) is run if a scanner is available in the environment; otherwise it is recorded as not run.
