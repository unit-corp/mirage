# Contributing

## Scope and lifecycle

Mirage-SQL `5.x` is the Current standard artifact line for active development. The `4.x` lines are maintenance-only. A contribution must target an existing branch and must not claim behavior that is not present in that branch.

For `4.x`, accept narrowly scoped security, defect, compatibility, build, and documentation fixes. New features belong on the Current development line after that line exists in source. Discuss public API, Maven-coordinate, package, Java-baseline, or framework-baseline changes before implementation.

Target `maintenance/{major}.{minor}.x` for standard artifacts and `maintenance/legacy/{major}.{minor}.x` for Legacy artifacts. Never merge a fix into the other artifact family without adapting and testing it against that family's coordinates, package namespace, and compatibility contract. The project may maintain only one minor line or multiple minor lines within either family. Check `SECURITY.md` and `COMPATIBILITY.md` before opening a change. Use a short-lived `fix/*` or `security/*` branch for an individual change, then apply it to every affected supported line. Forward-port the fix to newer affected lines, including Current development. Do not merge unrelated changes as part of the forward-port.

## Development setup

Use the JDK and Maven versions required by the target release line. Confirm them in [Compatibility](COMPATIBILITY.md) and the release POM. From the repository root:

```bash
mvn clean verify
```

The Maven reactor contains:

- `mirage`, which builds `vn.com.unit.miragesql:miragesql`
- `miragesql-test`
- `miragesql-integration`

## Change requirements

- Keep the change focused and consistent with the existing package and module structure.
- Add or update tests for behavior changes and regressions.
- Preserve backward compatibility on a maintenance line unless the change is an approved security fix that cannot be implemented compatibly.
- Do not add dependencies without explaining their runtime scope, license, security impact, and why existing JDK or framework capabilities are insufficient.
- Keep code comments, Javadoc, identifiers, exception messages, and log messages in English.
- Describe user-visible changes, compatibility impact, and migration requirements in the pull request so they can be added to the versioned changelog section during release preparation. Update `COMPATIBILITY.md` or the relevant versioned migration guide when runtime, dependency, API, coordinate, or configuration compatibility changes.
- Do not describe an artifact as released or available until the publication result is verified.

## Testing

Run the full build before submitting a change:

```bash
mvn clean verify
```

For a focused core change:

```bash
mvn -pl mirage -am test
```

Database-specific changes should include tests for the affected dialect and, where feasible, integration verification against the supported database and JDBC-driver versions. The existing HyperSQL-based suite alone is not evidence of compatibility with every implemented dialect.

## Commit and review information

Provide a concise problem statement, implementation rationale, compatibility impact, test evidence, and migration implications. Link the relevant issue when one exists. Clearly identify any remaining limitation or environment that was not tested.

Do not commit credentials, private repository settings, signing material, generated build output, or customer data.
