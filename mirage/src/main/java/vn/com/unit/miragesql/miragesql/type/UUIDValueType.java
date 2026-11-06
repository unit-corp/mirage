package vn.com.unit.miragesql.miragesql.type;

import java.nio.ByteBuffer;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.UUID;

import vn.com.unit.miragesql.miragesql.dialect.Dialect;
import vn.com.unit.miragesql.miragesql.dialect.UuidStorage;

/**
 * {@link ValueType} for {@link UUID}.
 * <p>
 * The way a value is written depends on {@link Dialect#getUuidStorage()}; reading accepts whatever the driver returns
 * ({@link UUID}, 16 bytes or a string).
 */
public class UUIDValueType extends AbstractValueType<UUID> implements DialectAware {

    private volatile UuidStorage storage = UuidStorage.STRING;

    public UUIDValueType() {
        super(UUID.class);
    }

    public UUIDValueType(Dialect dialect) {
        this();
        setDialect(dialect);
    }

    @Override
    public void setDialect(Dialect dialect) {
        this.storage = dialect != null ? dialect.getUuidStorage() : UuidStorage.STRING;
    }

    @Override
    public UUID get(Class<? extends UUID> type, ResultSet rs, int columnIndex) throws SQLException {
        return toUuid(rs.getObject(columnIndex));
    }

    @Override
    public UUID get(Class<? extends UUID> type, ResultSet rs, String columnName) throws SQLException {
        return toUuid(rs.getObject(columnName));
    }

    @Override
    public UUID get(Class<? extends UUID> type, CallableStatement cs, int index) throws SQLException {
        return toUuid(cs.getObject(index));
    }

    @Override
    public UUID get(Class<? extends UUID> type, CallableStatement cs, String parameterName) throws SQLException {
        return toUuid(cs.getObject(parameterName));
    }

    @Override
    public void set(Class<? extends UUID> type, PreparedStatement stmt, UUID value, int index) throws SQLException {
        switch (storage) {
        case NATIVE:
            if (value == null) {
                stmt.setNull(index, Types.OTHER);
            } else {
                stmt.setObject(index, value);
            }
            break;
        case BINARY:
            if (value == null) {
                stmt.setNull(index, Types.BINARY);
            } else {
                stmt.setBytes(index, toBytes(value));
            }
            break;
        default:
            if (value == null) {
                stmt.setNull(index, Types.VARCHAR);
            } else {
                stmt.setString(index, value.toString());
            }
            break;
        }
    }

    @Override
    public void registerOutParameter(Class<?> type, CallableStatement cs, int index) throws SQLException {
        cs.registerOutParameter(index, sqlType());
    }

    @Override
    public void registerOutParameter(Class<?> type, CallableStatement cs, String parameterName) throws SQLException {
        cs.registerOutParameter(parameterName, sqlType());
    }

    private int sqlType() {
        switch (storage) {
        case NATIVE:
            return Types.OTHER;
        case BINARY:
            return Types.BINARY;
        default:
            return Types.VARCHAR;
        }
    }

    /**
     * Converts the 16 bytes (big-endian, RFC 9562 order) to a {@link UUID}.
     */
    public static UUID fromBytes(byte[] bytes) {
        if (bytes.length != 16) {
            throw new IllegalArgumentException("UUID must be 16 bytes but was " + bytes.length);
        }
        ByteBuffer buf = ByteBuffer.wrap(bytes);
        return new UUID(buf.getLong(), buf.getLong());
    }

    /**
     * Converts a {@link UUID} to 16 bytes (big-endian, RFC 9562 order).
     */
    public static byte[] toBytes(UUID uuid) {
        return ByteBuffer.allocate(16).putLong(uuid.getMostSignificantBits())
                .putLong(uuid.getLeastSignificantBits()).array();
    }

    private static UUID toUuid(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof UUID) {
            return (UUID) value;
        }
        if (value instanceof byte[]) {
            return fromBytes((byte[]) value);
        }
        if (value instanceof CharSequence) {
            String s = value.toString().trim();
            if (s.isEmpty()) {
                return null;
            }
            if (s.length() == 32) {
                s = s.substring(0, 8) + "-" + s.substring(8, 12) + "-" + s.substring(12, 16) + "-"
                        + s.substring(16, 20) + "-" + s.substring(20);
            }
            return UUID.fromString(s);
        }
        throw new IllegalArgumentException("Cannot convert " + value.getClass().getName() + " to UUID");
    }
}
