package vn.com.unit.miragesql.miragesql.dialect;

import junit.framework.TestCase;
import vn.com.unit.miragesql.miragesql.annotation.PrimaryKey.GenerationType;
import vn.com.unit.miragesql.miragesql.type.OracleResultSetValueType;

public class OracleDialectTest extends TestCase {

	public void testGetName() {
		OracleDialect dialect = new OracleDialect();
		assertEquals("oracle", dialect.getName());
	}

	public void testNeedsParameterForResultSet() {
		OracleDialect dialect = new OracleDialect();
		assertTrue(dialect.needsParameterForResultSet());
	}

	public void testGetValueType() {
		OracleDialect dialect = new OracleDialect();
		assertTrue(dialect.getValueType() instanceof OracleResultSetValueType);
	}

	public void testGetSequenceSql() {
		OracleDialect dialect = new OracleDialect();
		String sql = dialect.getSequenceSql("SEQUENCE");
		assertEquals("SELECT SEQUENCE.NEXTVAL FROM DUAL", sql);
	}

	public void testSupportsGenerationType() {
		OracleDialect dialect = new OracleDialect();
		assertTrue(dialect.supportsGenerationType(GenerationType.APPLICATION));
		assertFalse(dialect.supportsGenerationType(GenerationType.IDENTITY));
		assertTrue(dialect.supportsGenerationType(GenerationType.SEQUENCE));
	}

}
