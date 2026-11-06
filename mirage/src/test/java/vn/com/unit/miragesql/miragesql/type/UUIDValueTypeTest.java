package vn.com.unit.miragesql.miragesql.type;

import java.util.UUID;

import junit.framework.TestCase;
import vn.com.unit.miragesql.miragesql.dialect.H2Dialect;
import vn.com.unit.miragesql.miragesql.dialect.MySQLDialect;
import vn.com.unit.miragesql.miragesql.dialect.OracleDialect;
import vn.com.unit.miragesql.miragesql.dialect.PostgreSQLDialect;
import vn.com.unit.miragesql.miragesql.dialect.SQLServerDialect;
import vn.com.unit.miragesql.miragesql.dialect.StandardDialect;
import vn.com.unit.miragesql.miragesql.dialect.UuidStorage;

public class UUIDValueTypeTest extends TestCase {

    private static final UUID ID = UUID.fromString("018f3b9e-7a10-7123-8456-0123456789ab");

    public void testIsSupport() {
        UUIDValueType valueType = new UUIDValueType();
        assertTrue(valueType.isSupport(UUID.class, null));
        assertFalse(valueType.isSupport(String.class, null));
    }

    public void testBytesAreBigEndianRfcOrder() {
        byte[] bytes = UUIDValueType.toBytes(ID);
        assertEquals(16, bytes.length);
        assertEquals(0x01, bytes[0]);
        assertEquals((byte) 0x8f, bytes[1]);
        assertEquals((byte) 0xab, bytes[15]);
        assertEquals(ID, UUIDValueType.fromBytes(bytes));
    }

    public void testFromBytesRejectsWrongLength() {
        try {
            UUIDValueType.fromBytes(new byte[8]);
            fail();
        } catch (IllegalArgumentException expected) {
            // ok
        }
    }

    public void testDialectStorage() {
        assertEquals(UuidStorage.NATIVE, new PostgreSQLDialect().getUuidStorage());
        assertEquals(UuidStorage.NATIVE, new H2Dialect().getUuidStorage());
        assertEquals(UuidStorage.BINARY, new MySQLDialect().getUuidStorage());
        assertEquals(UuidStorage.BINARY, new OracleDialect().getUuidStorage());
        assertEquals(UuidStorage.BINARY, new SQLServerDialect().getUuidStorage());
        assertEquals(UuidStorage.STRING, new StandardDialect().getUuidStorage());
    }
}
