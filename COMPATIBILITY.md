# Compatibility

This document distinguishes verified source compatibility from lifecycle policy. A lifecycle designation does not by itself prove that a release has been published.

## Release lines

| Line | Lifecycle | Coordinates | Java package | Minimum Java | Framework baseline | Evidence in this repository |
|---|---|---|---|---:|---|---|
| `5.0.x` standard artifact family | Current supported release line | `vn.com.unit.miragesql` with artifact names without `-legacy` | `vn.com.unit.miragesql` | 17 | Spring Boot 4 / Spring Framework 7 | Tag [`v5.0.0`](https://github.com/unit-corp/mirage/tree/v5.0.0) |
| `4.0.x` Legacy artifact family | Maintenance-only | `vn.com.unit.miragesql` with `-legacy` artifact names | `com.miragesql` | 17 | Spring Boot 4 / Spring Framework 7 | Tag [`legacy-v4.0.1`](https://github.com/unit-corp/mirage/tree/legacy-v4.0.1) |
| `3.0.x` | Internal support only | `com.miragesql` | `com.miragesql` | 17 | Spring Boot 3 / Spring Framework 6 | Internal UNIT line. Git history contains no `3.x` release tag in this repository |
| `2.1.2` and earlier | Upstream line, internal support only | `com.miragesql` | `com.miragesql` | 8 for `2.1.2` | Plain Spring Framework | Upstream tags through [`2.1.2`](https://github.com/unit-corp/mirage/tree/2.1.2). Not part of the UNIT public support policy |

## Maintenance branch policy

Artifact family and lifecycle status are separate concepts. The **Legacy artifact family** is the compatibility distribution with `-legacy` artifactIds and Java package `com.miragesql`. A standard artifact can also enter maintenance-only status when a newer release line becomes Current.

Legacy and standard artifacts must never share a maintenance branch. Their coordinates, Java packages, source layout, and compatibility contracts differ.

Within each artifact family, the project may use either maintenance model depending on customer commitments, security exposure, adoption, and maintainer capacity:

| Model | Branching | When to use |
|---|---|---|
| Single maintained minor line | Maintain only the latest supported minor for an artifact family | Default when users can upgrade within that artifact family and the project does not commit to overlapping minor support |
| Multiple maintained minor lines | Maintain each supported minor for an artifact family on its own branch | Use when different minor lines in the same artifact family must remain supported concurrently because of compatibility constraints or explicit support commitments |

Use these branch patterns:

- Standard artifacts: `maintenance/{major}.{minor}.x`, for example `maintenance/5.0.x`.
- Legacy artifacts: `maintenance/legacy/{major}.{minor}.x`, for example `maintenance/legacy/4.0.x`.

Supporting `maintenance/legacy/4.0.x` and `maintenance/5.0.x` at the same time means maintaining two different artifact families. It is not the same as supporting multiple minor lines within one artifact family. Creating a maintenance branch does not by itself establish support. The supported-version table in `SECURITY.md` and the release lifecycle declared here are authoritative.

Patch releases are immutable tags on the corresponding maintenance branch. Individual fixes may use short-lived `fix/*` or `security/*` branches, which are deleted after merge. New features do not belong on a maintenance branch.

The artifact-family mapping below describes stable coordinates and package layouts. Release-specific evidence remains in the release-line matrix above.

## Verified artifact families

The repository contains two different coordinate and package layouts. Do not treat them as interchangeable.

| Variant | Parent | Core | Test | Integration | Java package |
|---|---|---|---|---|---|
| Legacy-package variant | `miragesql-parent-legacy` | `miragesql-legacy` | `miragesql-test-legacy` | `miragesql-integration-legacy` | `com.miragesql` |
| Standard artifact variant | `miragesql-parent` | `miragesql` | `miragesql-test` | `miragesql-integration` | `vn.com.unit.miragesql` |

Both variants use groupId `vn.com.unit.miragesql`. The standard reactor contains three modules: `mirage`, `miragesql-test`, and `miragesql-integration`. The `mirage` directory produces artifact `miragesql`.

## Database support

The source contains dialect implementations for DB2, Derby, H2, HyperSQL, MySQL, Oracle Database, PostgreSQL, SQLite, SQL Server, and a standard JDBC dialect. Presence of a dialect class is not a support certification for every database or driver version.

The repository test suite primarily uses HyperSQL and includes dialect-focused unit tests. A production adoption should validate the exact database edition, JDBC driver, SQL templates, stored procedures, transaction boundaries, and connection pool used by the application.

## Integration compatibility

The `5.0.x` standard artifact line provides optional integration code for Spring and Guice and retains Seasar2 integration code. Spring dependencies are compiled against the Spring Boot 4 dependency-management baseline. Applications using another Spring generation must resolve and test dependency compatibility explicitly rather than importing the Mirage-SQL parent POM as a BOM without review.

## Verification status

Confirm artifact availability in the target repository before declaring a production dependency. A version in `CHANGELOG.md` or a local Maven build does not by itself prove repository availability.
