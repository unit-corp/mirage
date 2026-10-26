# Data Model: Update OGNL Library

No persistent data is introduced or changed. The two spec entities map to:

| Entity | Representation | Notes |
|--------|----------------|-------|
| OGNL dependency | `ognl:ognl` entry in `mirage/pom.xml` | Version 3.2.4 → 3.4.17; stable only |
| SQL template expression | Expression string in a 2-way SQL template, parsed once and evaluated per call by `OgnlUtil` | Evaluation result and error type (`OgnlRuntimeException` with path and line number) must remain identical |

No state transitions. Validation rule: the declared version must not be a pre-release.
