# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Overview

Mirage-SQL is a SQL-centric database access library for Java ("2Way SQL" templates in plain `.sql` files). This is UNIT Technology Corporation's 2025 fork of `mirage-sql/mirage`; the Java package root is `vn.com.unit.miragesql.miragesql` and the Maven groupId is `vn.com.unit.miragesql`. Requires Java 17; Spring Boot BOM 3.2.0 / Spring Framework 6.

## Commands

Maven multi-module build (parent `miragesql-parent`, version `3.1.0-SNAPSHOT`; modules: `mirage`, `miragesql-test`, `miragesql-integration`):

```bash
mvn clean install                      # build + test everything
mvn -pl mirage test                    # one module
mvn -pl mirage test -Dtest=SqlParserImplTest                 # single test class
mvn -pl mirage test -Dtest=SqlManagerImplTest#testGetSingleResult   # single test method
mvn -pl mirage -am -DskipTests install # install core so dependent modules resolve it
```

Change the project version (parent and all module `<parent>` references) in one step; no plugin is added to the poms for this:

```bash
mvn versions:set -DnewVersion=3.1.0 -DgenerateBackupPoms=false
```

No linter is configured. CI (`.github/workflows/ci.yml`) only triggers a shared snapshot-release workflow on pushes to `develop`; it does not run tests itself.

## Architecture

- **`mirage`** (artifact `miragesql`) — the core library, no web/Spring dependency in the API surface (Lombok is `provided`).
  - `SqlManager` / `SqlManagerImpl` is the main facade (query, update, batch, call, entity CRUD). It delegates to `SqlExecutor` / `CallExecutor` (JDBC) and `EntityOperator` (row ↔ bean mapping).
  - `SqlResource` (`ClasspathSqlResource`, `StringSqlResource`) locates SQL; by convention a classpath `.sql` file next to the entity/DAO (e.g. `SqlManagerImplTest_selectByBookName.sql`).
  - `parser/` implements the 2Way SQL engine: `SqlTokenizer` → `SqlParserImpl` builds a `Node` tree (`IfNode`, `BeginNode`, `BindVariableNode`, `EmbeddedValueNode`, `ParenBindVariableNode`, `PrefixSqlNode`, …) evaluated against a `SqlContext`; conditions are OGNL expressions (`util/OgnlUtil`, `SqlContextPropertyAccessor`).
  - `dialect/` — per-database `Dialect` (HSQLDB, H2, MySQL, Oracle, PostgreSQL, SQL Server, DB2, Derby, SQLite, Standard) handling pagination, sequences, type mapping, etc. `session/DialectAutoSelector` picks one from connection metadata.
  - `session/` — `Session` / `SessionFactory` with JDBC, DBCP, HikariCP and JNDI implementations; manages a thread-bound connection and transaction. `provider/` supplies `ConnectionProvider`s (DataSource, JNDI, XA). `filter/OpenSessionInViewFilter` is the servlet integration.
  - `bean/` (`BeanDesc`, `PropertyExtractor`) is the reflection layer; `annotation/` (`@Table`, `@Column`, `@PrimaryKey`, `@Transient`, `@In/@Out/@InOut/@ResultSet` for stored procs); `naming/` converts Java ↔ DB names; `type/` has `ValueType` converters for JDBC get/set; `updater/` and `util/` are helpers.
- **`miragesql-test`** — test support: `MockSqlManager`, `MockSqlExecuter`, `MockCallExecuter`, `MirageTestContext` record executed SQL (`ExecutedSQLInfo`) so applications can unit-test DAO code without a DB.
- **`miragesql-integration`** — adapters for Spring (`SpringConnectionProvider`), Guice (`MirageModule`, `@Transactional`, `TransactionInterceptor`) and Seasar2 (`SeasarConnectionProvider`).

## Testing notes

- Tests are mostly JUnit 3 style (`extends TestCase`) with Mockito, running against in-memory HSQLDB (`jdbc:hsqldb:mem:mirage_test`). `AbstractDatabaseTest` mocks a `ConnectionProvider` and runs `SqlManagerImplTest_setUp.sql` / `_tearDown.sql` from the classpath.
- `AbstractDatabaseTest`, `SqlManagerImplTest` and the `SqlManagerImplTest_*.sql` fixtures are duplicated in each module's `src/test`; when changing one, keep the copies in `mirage`, `miragesql-test` and `miragesql-integration` in sync.

## Related repositories

The session also has `spring-data-mirage` (Spring Data layer on top of this library) and `jcanary-db-connector` as sibling checkouts; changes to public APIs here (`SqlManager`, `Dialect`, `Session`) may affect them.
