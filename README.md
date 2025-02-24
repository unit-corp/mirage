# Mirage-SQL

This project is a fork of [mirage](https://github.com/mirage-sql/mirage).
Modifications have been made by UNIT Technology Corporation in 2025.

**Mirage-SQL** is an easy and powerful SQL-centric database access library for Java (or JVM based languages) which provides dynamic SQL templates in plain SQL files.

## Requirements

* Java 17+
* Spring Framework 6

## Quick Start

You can get **Mirage-SQL** from the Maven Central repository. Add the following fragment into your `pom.xml`.

```xml
<dependency>
    <groupId>vn.com.unit.miragesql</groupId>
    <artifactId>miragesql</artifactId>
    <version>3.x.x</version>
</dependency>
```
or in a Gradle based project add to your `build.gradle` the following line:
```groovy
compile 'vn.com.unit.miragesql:miragesql:3.x.x'
```

or just download it from the [Release Page](https://github.com/unit-corp/mirage/releases).


Other **`Mirage-SQL`** Modules:

Module|Description|Gradle
---   |---        |---
**Mirage-SQL Test**|The testing functionality.| `testCompile 'vn.com.unit.miragesql:miragesql-test:3.x.x'`
**Mirage-SQL Integration** |The integration with [Spring](https://projects.spring.io/spring-framework/), [Guice](https://github.com/google/guice) and [Seasar2](http://www.seasar.org/en/)..|`compile 'vn.com.unit.miragesql:miragesql-integration:3.x.x'`


If you are updating your application from a previous **`Mirage-SQL`** version, see the [Migration Guide](https://github.com/unit-corp/mirage/wiki/Migration-Guide).

## Example

This is a simple example of a SQL template:

```sql
SELECT * FROM BOOK
/*BEGIN*/
  WHERE
  /*IF author != null */
        AUTHOR = /*author*/'Naoki Takezoe'
  /*END*/
  /*IF minPrice != null */
    AND PRICE >= /*minPrice*/20
  /*END*/
  /*IF maxPrice != null */
    AND PRICE <= /*maxPrice*/100
  /*END*/
/*END*/
ORDER BY BOOK_ID ASC
```

With **Mirage-SQL** you can embed variables or conditions using `special` SQL comments, so it's a plain SQL that can be run with any SQL client tool directly. 
This feature used in the Mirage's SQL template it's called [2Way SQL](https://github.com/unit-corp/mirage/wiki/2WaySQL).

## Links:
 - A **detailed documentation** is provided in the [Wiki](https://github.com/unit-corp/mirage/wiki).
 - If you find any **bugs or issues**, please report them in the [GitHub Issue Tracker](https://github.com/unit-corp/mirage/issues).
 - **[Release Notes](https://github.com/unit-corp/mirage/wiki/Releases)** of all previous Mirage-SQL versions.
 - Mirage-SQL also has support for other **JVM based languages**