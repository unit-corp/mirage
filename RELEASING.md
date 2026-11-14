# Releasing Mirage-SQL

This repository uses a multi-module Maven reactor. The parent and all three published modules must use the same release version.

## Release policy

- `5.x` is the Current standard artifact line. Active development occurs on `develop`, while `main` contains the latest completed standard release.
- Standard and Legacy artifacts are separate release families. Standard maintenance branches use `maintenance/{major}.{minor}.x`. Legacy maintenance branches use `maintenance/legacy/{major}.{minor}.x`.
- A maintenance policy may cover a major line, one or more minor lines, or a short-lived exact patch branch within either artifact family.
- A release fact is established only after the release commit, Git tag, build result, and artifact publication have been verified.
- Never reuse or move a published release tag.

Use a major branch when the whole major line shares one maintenance stream. Use minor branches when supported minors require independent streams. Use an exact patch branch only as a short-lived branch for preparing that patch release. Record the selected model, artifact family, and supported scope in `COMPATIBILITY.md` and `SECURITY.md`.

Each long-lived maintenance stream advances independently. After releasing from a minor-scoped branch, bump it to the next patch `-SNAPSHOT`. Delete an exact patch branch after its immutable tag is created and the fix is propagated to every affected long-lived branch.

Fixes affecting multiple supported minor lines must be applied and tested on every affected branch. A fix affecting both standard and Legacy artifacts must be adapted and tested separately for each family. Forward-port the same logical fix to newer affected lines. Do not merge one maintenance branch wholesale into another because package layout, coordinates, dependencies, and compatibility contracts may differ.

## Branch version policy

| Branch or ref | Version policy | Example |
|---|---|---|
| `main` | Exact non-SNAPSHOT version of the latest completed standard release | After releasing `5.0.0`, `main` remains `5.0.0` |
| `develop` | Next planned minor or major `-SNAPSHOT` | After releasing `5.0.0`, use `5.1.0-SNAPSHOT` |
| Short-lived `feature/*` branch | Inherit the version from `develop` | A feature for the next minor release uses `5.1.0-SNAPSHOT` |
| Optional `release/{version}` branch | Exact target release version during final stabilization | `release/5.1.0` uses `5.1.0` before merging to `main` |
| `maintenance/{major}.x` | Snapshot within the selected standard major | `maintenance/5.x` accepts `5.y.z-SNAPSHOT` |
| `maintenance/{major}.{minor}.x` | Next standard patch `-SNAPSHOT` | `maintenance/5.0.x` accepts `5.0.z-SNAPSHOT` |
| `maintenance/{major}.{minor}.{patch}` | Exact short-lived standard patch `-SNAPSHOT` | `maintenance/5.0.1` requires `5.0.1-SNAPSHOT` |
| `maintenance/legacy/{major}.x` | Snapshot within the selected Legacy major | `maintenance/legacy/4.x` accepts `4.y.z-SNAPSHOT` |
| `maintenance/legacy/{major}.{minor}.x` | Next Legacy patch `-SNAPSHOT` | `maintenance/legacy/4.0.x` accepts `4.0.z-SNAPSHOT` |
| `maintenance/legacy/{major}.{minor}.{patch}` | Exact short-lived Legacy patch `-SNAPSHOT` | `maintenance/legacy/4.0.2` requires `4.0.2-SNAPSHOT` |
| Short-lived `fix/*` or `security/*` branch | Inherit the version from its target maintenance branch | A fix targeting `maintenance/5.0.x` uses `5.0.1-SNAPSHOT` |
| Release commit and immutable tag | Exact non-SNAPSHOT release version | Tag `v5.0.0` contains Maven version `5.0.0` |

During standard release preparation, stabilize the exact release version on an optional `release/{version}` branch or an equivalent controlled release commit. Merge the verified release to `main` and create the immutable tag from that release commit. After publication, `main` remains on the released non-SNAPSHOT version and `develop` advances to the next `-SNAPSHOT` version.

Long-lived maintenance branches remain on an appropriate next `-SNAPSHOT` between releases. For a patch release, create an exact-version release commit and tag on the corresponding maintenance line. Then advance a continuing minor branch to the next patch `-SNAPSHOT`, or delete a short-lived exact patch branch. Forward-port the fix to `develop` when the newer line is affected. Do not publish a SNAPSHOT as a production release.

The snapshot workflow in `.github/workflows/release.yml` runs on pushes matching:

```text
develop
maintenance/[0-9]+.x
maintenance/[0-9]+.[0-9]+.x
maintenance/[0-9]+.[0-9]+.[0-9]+
maintenance/legacy/[0-9]+.x
maintenance/legacy/[0-9]+.[0-9]+.x
maintenance/legacy/[0-9]+.[0-9]+.[0-9]+
```

`main` is intentionally excluded from snapshot triggers. These GitHub branch filters allow only numeric major, minor, and patch components in the documented branch shapes. The shared deployment script separately requires the Maven project version to end in `-SNAPSHOT` and skips branch deployment otherwise.

The GitHub Actions workflow delegates release and snapshot deployment to `unit-corp/ci-templates-public/.github/workflows/trigger-maven-central-release.yml@main`. Verify the shared workflow revision, runner configuration, repository variables, credentials, and an end-to-end dry run before relying on it for production release automation.

## Preconditions

1. Confirm the release branch, lifecycle eligibility, and clean working tree.
2. Confirm the intended version does not already exist as a Git tag or published artifact.
3. Review `README.md`, `CHANGELOG.md`, `COMPATIBILITY.md`, `MIGRATION.md`, the relevant versioned migration guide, `SECURITY.md`, and Maven metadata.
4. Verify all module parent versions and the root project version are identical.
5. Confirm JDK, Maven, signing key, and Maven Central credentials in an approved release environment. Do not store credentials in the repository.

## Build and verification

Run from the repository root:

```bash
mvn clean verify
```

Inspect the generated JARs, sources JARs, Javadoc JARs, POMs, `META-INF/LICENSE`, `META-INF/NOTICE`, and generated Git properties. Confirm dependency scopes and artifact coordinates for every module.

For a release candidate, also validate the documented database and integration matrix. A successful HyperSQL test suite is necessary but not sufficient evidence for all database dialects.

## Version and changelog

Set the same non-SNAPSHOT version in the root POM and every module parent declaration. Create the versioned changelog section using only changes verified from the release diff, pull requests, Git history, and test evidence.

Before treating a version as released, verify that its release commit, immutable tag, publication result, and changelog entry agree. Resolve any discrepancy before announcing the release.

## Publish

Publish releases only from an approved release environment using the project's validated publishing procedure.

Before publication, verify:

- release coordinates and version
- signing identity and credential source
- target artifact repository
- generated POM, JAR, sources, and Javadoc artifacts
- license and notice files
- publication result in the target repository

Do not document or execute a publishing command until the procedure has been validated end to end. Review the repository result and verify all expected coordinates after publication. A successful local build does not prove that artifacts were published or are publicly resolvable.

## Tag and post-release verification

Create an annotated tag only for the immutable release commit, using the documented tag convention for that release line. Do not introduce a new convention during release execution without prior agreement.

After publication:

1. Resolve each artifact from a clean environment.
2. Verify signatures and checksums where provided.
3. Verify the tag points to the reviewed release commit.
4. Confirm release documentation links and examples use the published version.
5. Announce only the capabilities and compatibility actually verified for that release.

If publication partially fails, do not overwrite an artifact already accepted by the repository. Determine which coordinates are immutable, correct the release plan, and use a new version when required.
