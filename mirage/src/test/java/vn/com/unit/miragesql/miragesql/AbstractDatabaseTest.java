package vn.com.unit.miragesql.miragesql;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;

import junit.framework.TestCase;
import vn.com.unit.miragesql.miragesql.dialect.HyperSQLDialect;
import vn.com.unit.miragesql.miragesql.provider.ConnectionProvider;
import vn.com.unit.miragesql.miragesql.util.IOUtil;

public abstract class AbstractDatabaseTest extends TestCase {

	protected static final String SQL_PREFIX = "vn/com/unit/miragesql/miragesql/";

	protected SqlManager sqlManager = new SqlManagerImpl();
	protected Connection conn;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		Class.forName("org.hsqldb.jdbc.JDBCDriver");
		conn = DriverManager.getConnection("jdbc:hsqldb:mem:mirage_test", "sa", "");

		ConnectionProvider connectionProvider = mock(ConnectionProvider.class);
		when(connectionProvider.getConnection()).thenReturn(conn);

		sqlManager.setConnectionProvider(connectionProvider);
		sqlManager.setDialect(new HyperSQLDialect());

		executeMultipleStatement(SQL_PREFIX + "SqlManagerImplTest_setUp.sql");
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		executeMultipleStatement(SQL_PREFIX + "SqlManagerImplTest_tearDown.sql");
		conn.close();
	}

	private void executeMultipleStatement(String sqlPath) throws Exception {
		ClassLoader cl = Thread.currentThread().getContextClassLoader();
		byte[] bytes = IOUtil.readStream(cl.getResourceAsStream(sqlPath));
		String sql = new String(bytes, StandardCharsets.UTF_8);
		for(String statement: sql.split(";")){
			if(statement.trim().length() > 0){
				sqlManager.executeUpdate(new StringSqlResource(statement));
			}
		}
	}

}
