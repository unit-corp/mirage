package vn.com.unit.miragesql.miragesql.provider;

import java.sql.Connection;

import vn.com.unit.miragesql.miragesql.SqlManager;
import vn.com.unit.miragesql.miragesql.exception.SQLRuntimeException;

/**
 * The interface of the connection provider.
 * <p>
 * Mirage-SQL uses this interface to get the connection to the database.
 *
 * @author Naoki Takezoe
 * @see SqlManager#setConnectionProvider(ConnectionProvider)
 */
public interface ConnectionProvider {

    /**
     * Returns the connection to the database.
     *
     * @return the connection
     * @throws SQLRuntimeException Failed to get the connection
     */
    Connection getConnection();

}
