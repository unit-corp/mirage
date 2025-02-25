package vn.com.unit.miragesql.miragesql.session;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import vn.com.unit.miragesql.miragesql.SqlManager;
import vn.com.unit.miragesql.miragesql.SqlManagerImpl;
import vn.com.unit.miragesql.miragesql.dialect.Dialect;
import vn.com.unit.miragesql.miragesql.exception.SessionException;
import vn.com.unit.miragesql.miragesql.provider.DefaultConnectionProvider;
import vn.com.unit.miragesql.miragesql.util.StringUtil;

/**
 * The default implementation of {@link Session}.
 * <p>
 * This implementation set {@link Dialect} to {@link SqlManager} automatically by the JDBC connection URL.
 *
 * @author Naoki Takezoe
 */
public class JDBCSessionImpl implements Session {

    private static final Logger logger = LoggerFactory.getLogger(JDBCSessionImpl.class);

    private SqlManager sqlManager;
    private DefaultConnectionProvider provider;
    private String driver;
    private String url;
    private String user;
    private String password;
    private ThreadLocal<Boolean> rollbackOnly = new ThreadLocal<>();

    /**
     * The implementation of {@link Session} which creates the connection from a JDBC connection.
     *
     * To enable it you need to add the properties below to <code>jdbc.properties</code>.
     *
     * @param properties the Properties object which has a following properties:
     *   <ul>
     *     <li>jdbc.driver - the JDBC driver classname (optional on JDBC 4.0)</li>
     *     <li>jdbc.url - the JDBC connection URL</li>
     *     <li>jdbc.user - the username</li>
     *     <li>jdbc.password - the password</li>
     *     <li>sql.cache - if true then SqlManager caches parsing result of 2waySQL</li>
     *   </ul>
     */
    public JDBCSessionImpl(Properties properties){
        this.driver   = properties.getProperty("jdbc.driver");
        this.url      = properties.getProperty("jdbc.url");
        this.user     = properties.getProperty("jdbc.user");
        this.password = properties.getProperty("jdbc.password");

        sqlManager = new SqlManagerImpl();
        sqlManager.setDialect(DialectAutoSelector.getDialect(url));
        provider = new DefaultConnectionProvider();
        sqlManager.setConnectionProvider(provider);

        String cache = properties.getProperty("sql.cache");
        if("true".equals(cache)){
            ((SqlManagerImpl) sqlManager).setCacheMode(true);
        } else {
            ((SqlManagerImpl) sqlManager).setCacheMode(false);
        }
    }

    /**{@inheritDoc}*/
    public void begin() {
        if(logger.isInfoEnabled()){
            logger.info("Begin transaction.");
        }
        try {
            if(StringUtil.isNotEmpty(driver)){
                Class.forName(driver);
            }
            Connection conn = DriverManager.getConnection(url, user, password);
            conn.setAutoCommit(false);
            provider.setConnection(conn);
        } catch (ClassNotFoundException ex) {
            throw new SessionException("Driver class not found.", ex);

        } catch (SQLException ex){
            throw new SessionException("Failed to begin transaction.", ex);

        }
    }

    /**{@inheritDoc}*/
    public void commit() {
        if(logger.isInfoEnabled()){
            logger.info("Commit transaction.");
        }
        try {
            provider.getConnection().commit();
        } catch (SQLException ex){
            throw new SessionException("Failed to commit transaction.", ex);
        }
    }

    /**{@inheritDoc}*/
    public SqlManager getSqlManager() {
        return sqlManager;
    }

    public void release() {
        this.rollbackOnly.remove();

        if(provider instanceof DefaultConnectionProvider){
            ((DefaultConnectionProvider) provider).releaseConnection();
        }
    }

    /**{@inheritDoc}*/
    public void rollback() {
        if(logger.isInfoEnabled()){
            logger.info("Rollback transaction.");
        }
        try {
            provider.getConnection().rollback();
        } catch (SQLException ex){
            throw new SessionException("Failed to rollback transaction.", ex);
        }
    }

    /**{@inheritDoc}*/
    public void setRollbackOnly() {
        this.rollbackOnly.set(true);
    }

    /**{@inheritDoc}*/
    public boolean isRollbackOnly() {
        return this.rollbackOnly.get() != null;
    }

}
