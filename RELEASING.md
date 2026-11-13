# Releasing Mirage-SQL

This repository uses a multi-module Maven reactor. The parent and all three published modules must use the same release version.

## Release policy

- `5.x` is the Current line for active development.
- Standard and Legacy artifacts are separate release families. Standard maintenance branches use `maintenance/{major}.{minor}.x`. Legacy maintenance branches use `maintenance/legacy/{major}.{minor}.x`.
- A maintenance policy may cover one minor line or multiple minor lines within either artifact family. Each supported minor uses a separate long-lived branch.
- A release fact is established only after the release commit, Git tag, build result, and artifact publication have been verified.
- Never reuse or move a published release tag.

Use the single-line model when only the latest minor within an artifact family is supported. Use the multiple-line model only when compatibility constraints or explicit support commitments require overlapping minor support in that family. Record the selected model, artifact family, and exact supported minor lines in `COMPATIBILITY.md` and `SECURITY.md`.

Each maintenance branch advances independently through patch releases. After releasing a patch, bump that branch to the next patch `-SNAPSHOT`.

Fixes affecting multiple supported minor lines must be applied and tested on every affected branch. A fix affecting both standard and Legacy artifacts must be adapted and tested separately for each family. Forward-port the same logical fix to newer affected lines. Do not merge one maintenance branch wholesale into another because package layout, coordinates, dependencies, and compatibility contracts may differ.

The inspected repository contains Maven Central publishing configuration but no automated release workflow. The only GitHub Actions workflow is an older Gradle workflow and does not define the Maven release process. Until automation is added and validated, treat publishing as a controlled manual operation.

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
