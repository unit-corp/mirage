package vn.com.unit.miragesql.miragesql.dialect;

import junit.framework.TestCase;
import vn.com.unit.miragesql.miragesql.annotation.PrimaryKey.GenerationType;
import vn.com.unit.miragesql.miragesql.dialect.PostgreSQLDialect;
import vn.com.unit.miragesql.miragesql.type.PostgreResultSetValueType;

public class PostgreSQLDialectTest extends TestCase {

	public void testGetName() {
		PostgreSQLDialect dialect = new PostgreSQLDialect();
		assertEquals("postgresql", dialect.getName());
	}

	public void testGetValueType() {
		PostgreSQLDialect dialect = new PostgreSQLDialect();
		assertTrue(dialect.getValueType() instanceof PostgreResultSetValueType);
	}

	public void testGetSequenceSql() {
		PostgreSQLDialect dialect = new PostgreSQLDialect();
		String sql = dialect.getSequenceSql("SEQUENCE");
		assertEquals("SELECT NEXTVAL('SEQUENCE')", sql);
	}

	public void testSupportsGenerationType() {
		PostgreSQLDialect dialect = new PostgreSQLDialect();
		assertTrue(dialect.supportsGenerationType(GenerationType.APPLICATION));
		assertFalse(dialect.supportsGenerationType(GenerationType.IDENTITY));
		assertTrue(dialect.supportsGenerationType(GenerationType.SEQUENCE));
	}

}
