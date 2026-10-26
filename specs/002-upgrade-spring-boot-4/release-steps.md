# Release Steps (for the maintainer)

Do not run these automatically. They create branches, tags and published artifacts, and need the maintainer's decision. Names follow `plan.md` "Release Line Naming".

State at the time of writing: work is on local branch `feature/spring-boot-4` (from `feature/upgrade-libs-3.0.2`), uncommitted; nothing pushed.

## 1. Commit the upgrade

- Commit the POM changes, `CHANGELOG.md` and `specs/002-upgrade-spring-boot-4/` on `feature/spring-boot-4`.

## 2. Spring Boot 3 line (`3.0.x`)

- Create `release/3.0.2` from `feature/upgrade-libs-3.0.2` (the OGNL and JUnit work), then create `support/3.x` from `release/3.0.2` for future Spring Boot 3 fixes.
- Reconcile the existing local branch `chore/version-3.0.2` (it is checked out in the worktree `.claude/worktrees/bump-302`) with `release/3.0.2`: decide whether it is the same work or is replaced.
- Publish `3.0.2` from that line when ready; its dependency tree must still show Spring Boot 3.4.x.

## 3. Spring Boot 4 line (`4.0.x`)

- Merge `feature/spring-boot-4` into `release/4.0.1`, then tag `4.0.1`.
- Build with `mvn clean verify` and publish the three artifacts (`miragesql`, `miragesql-integration`, `miragesql-test`) at version `4.0.1` to the internal releases repository configured in the root `pom.xml` (`distributionManagement`). A published version is immutable there, so do not reuse a number.
- Make `main` follow the `4.x` line afterwards.

## 4. Before publishing

- Run a full dependency vulnerability scan if possible; only a GHSA spot check was done (see `research.md`).
- Confirm the changelog text in `CHANGELOG.md` matches what is released.
