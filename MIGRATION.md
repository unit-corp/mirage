# Migration Guide

This file is the migration index. Detailed instructions are maintained in versioned guides under `docs/migration/`.

## Supported migration paths

| From | To | Guide | Status |
|---|---|---|---|
| `4.x` | `5.x` | [Migrating from 4.x to 5.x](docs/migration/4-to-5.md) | Guide framework available. Release-specific steps are pending a verifiable `5.x` source baseline |

## Versioning convention

Each major-version transition has one guide named:

```text
docs/migration/{from-major}-to-{to-major}.md
```

Add a new guide when a new major version introduces a supported migration path. Keep this index short and link to the versioned guide rather than duplicating detailed instructions here.

## Before any major upgrade

1. Identify the exact source and target versions, Maven coordinates, Java package, Java runtime, Spring generation, database, and JDBC driver.
2. Read [Compatibility](COMPATIBILITY.md) and both release sections in [Changelog](CHANGELOG.md).
3. Build a dependency tree and check for mixed Mirage-SQL generations or duplicate legacy/current artifacts.
4. Back up application configuration and establish a rollback point. Mirage-SQL does not manage application data migration, so database backup requirements remain application-specific.
5. Run unit, integration, SQL-template, transaction, stored-procedure, and representative performance tests against the target database.

Do not infer migration steps from a planned version number. A guide becomes release-ready only after its coordinates, package layout, runtime baseline, breaking changes, and verification commands can be checked against source and release artifacts.
