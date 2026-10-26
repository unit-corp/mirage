# Changelog

## Release lines

Both lines use the same group (`com.miragesql`) and artifact names (`miragesql`, `miragesql-integration`, `miragesql-test`). The version number selects the line, and the major number matches the Spring Boot generation.

| Line | Spring Boot | Spring Framework | Minimum Java |
|------|-------------|------------------|--------------|
| `3.0.x` | 3.4.x | 6.2.x | 21 |
| `4.0.x` | 4.1.x | 7.0.x | 21 |

Use `4.0.x` if your application is on Spring Boot 4; stay on `3.0.x` if it is on Spring Boot 3.

## 4.0.1

### Changed

- Spring Boot upgraded from 3.4.0 to **4.1.2** (Spring Framework 6.2.0 to 7.0.10, JUnit Platform 1.11.3 to 6.0.3).
- Project version moved from 3.0.1 to **4.0.1** for all modules. Group and artifact names are unchanged.

### Compatibility

- Minimum Java version is unchanged: **Java 21**.
- No public class or method of the library changed; no source change is required on the consumer side other than moving the application to Spring Boot 4.
- Existing SQL templates, Spring wiring and transaction handling behave as in 3.0.1 (all 80 existing tests pass unchanged).
- This release requires Spring Boot 4 / Spring Framework 7. Applications that stay on Spring Boot 3 must keep using the `3.0.x` line.

### Dependencies carried over from the 3.0.2 work

- OGNL 3.4.17 (was 3.2.4), and JUnit 4 tests run through `junit-vintage-engine` on the JUnit Platform.

### Notes

- `spring-boot-starter-tomcat` (used by the module that provides the servlet filter and listener) now brings more Spring Boot modules transitively than it did on Spring Boot 3.
