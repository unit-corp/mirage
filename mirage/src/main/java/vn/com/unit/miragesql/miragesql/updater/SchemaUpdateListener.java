package vn.com.unit.miragesql.miragesql.updater;

import java.sql.Connection;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import vn.com.unit.miragesql.miragesql.SqlManagerImpl;
import vn.com.unit.miragesql.miragesql.session.Session;
import vn.com.unit.miragesql.miragesql.session.SessionFactory;
import vn.com.unit.miragesql.miragesql.util.ExceptionUtil;
import vn.com.unit.miragesql.miragesql.util.StringUtil;

/**
 * This is a ServletContextListener to execute {@link SchemaUpdater} when the servlet context is initialized.
 * <p>
 * This listener is only available when Mirage-SQL is working standalone.
 * Because this listener get connection configurations from jdbc.properties using {@link SessionFactory}.
 * If you want to use Mirage-SQL with DI containers such as Spring or etc, you can't use this listener.
 * </p>
 *
 * @author Naoki Takezoe
 */
public class SchemaUpdateListener implements ServletContextListener {

    private static final Logger logger = LoggerFactory.getLogger(SchemaUpdateListener.class);

    public void contextInitialized(ServletContextEvent sce) {

        SchemaUpdater updater = new SchemaUpdater();
        Session session = SessionFactory.getSession();

        updater.setSqlManager(session.getSqlManager());

        String packageName =
            sce.getServletContext().getInitParameter("SCHEMA_UPDATE_SQL_PACKAGE");

        if(StringUtil.isNotEmpty(packageName)){
            updater.setPackageName(packageName);
        }

        try {
            session.begin();

            Connection conn = ((SqlManagerImpl) session.getSqlManager())
                .getConnectionProvider().getConnection();

            try {
                conn.setAutoCommit(true);


            } catch (SQLException ex){
                logger.error("Failed to update schema.");
                logger.error(ExceptionUtil.toString(ex));

            } finally {
                try {
                    conn.setAutoCommit(false);
                } catch (SQLException ex) {
                    logger.error(ExceptionUtil.toString(ex));
                }
            }
        } finally {
            session.release();
        }
    }

    public void contextDestroyed(ServletContextEvent sce) {
    }

}
