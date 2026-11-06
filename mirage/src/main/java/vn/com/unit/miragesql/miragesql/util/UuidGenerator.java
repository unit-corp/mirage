package vn.com.unit.miragesql.miragesql.util;

import java.util.UUID;

/**
 * Generates UUIDs for properties annotated with
 * {@link vn.com.unit.miragesql.miragesql.annotation.PrimaryKey.GenerationType#UUID_V7}.
 */
@FunctionalInterface
public interface UuidGenerator {

    UUID generate();

}
