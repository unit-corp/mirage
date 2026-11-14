package vn.com.unit.miragesql.miragesql.dialect;

/**
 * How a {@link java.util.UUID} is stored in a database column.
 * <p>
 * Binary and string forms keep the RFC 9562 byte/character order, so time-ordered UUIDs (v7) stay sortable.
 */
public enum UuidStorage {

    /** Native UUID column type (e.g. PostgreSQL <code>uuid</code>), bound with <code>setObject</code>. */
    NATIVE,

    /** 16 bytes, big-endian (e.g. <code>BINARY(16)</code>, <code>RAW(16)</code>). */
    BINARY,

    /** 36 characters in canonical form (<code>CHAR(36)</code>). */
    STRING

}
