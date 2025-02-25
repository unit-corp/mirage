package vn.com.unit.miragesql.miragesql.provider;

import java.sql.Connection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import vn.com.unit.miragesql.miragesql.util.JdbcUtil;

/**
 * The simple implementation of {@link ConnectionProvider}.
 *
 * @author Naoki Takezoe
 */
public class DefaultConnectionProvider implements ConnectionProvider {

    private static final Logger logger = LoggerFactory.getLogger(DefaultConnectionProvider.class);

    private ThreadLocal<Connection> threadLocal = new ThreadLocal<>();

    public void setConnection(Connection conn){
        threadLocal.set(conn);
    }

    public void releaseConnection(){
        Connection conn = threadLocal.get();
        if(conn != null){
            JdbcUtil.close(conn);
            threadLocal.remove();

            logger.info("Connection is released.");

        } else {
            logger.info("Connection is not used.");
        }
    }

//	@Override
    /**{@inheritDoc}*/
    public Connection getConnection() {
        return threadLocal.get();
    }

}
