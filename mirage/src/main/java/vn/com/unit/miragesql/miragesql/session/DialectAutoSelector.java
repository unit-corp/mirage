package vn.com.unit.miragesql.miragesql.session;

import vn.com.unit.miragesql.miragesql.dialect.*;

public class DialectAutoSelector {

    /**
     * Selects the Database Dialect based on the JDBC connection URL.
     *
     * @param url the JDBC Connection URL
     * @return the dialect that maps to a specific JDBC URL.
     */
    public static Dialect getDialect(String url){
        if(url.startsWith("jdbc:mysql:")){
            return new MySQLDialect();
        } else if(url.startsWith("jdbc:postgresql:")){
            return new PostgreSQLDialect();
        } else if(url.startsWith("jdbc:oracle:")){
            return new OracleDialect();
        } else if(url.startsWith("jdbc:hsqldb:")){
            return new HyperSQLDialect();
        } else if(url.startsWith("jdbc:h2:")){
            return new H2Dialect();
        } else if(url.startsWith("jdbc:derby:")){
            return new DerbyDialect();
        } else if(url.startsWith("jdbc:sqlite:")){
            return new SQLiteDialect();
        } else if(url.startsWith("jdbc:sqlserver:") || url.startsWith("jdbc:jtds:sqlserver:")){
            return new SQLServerDialect();
        } else if(url.startsWith("jdbc:db2:") || url.startsWith("jdbc:db2j:")
                || url.startsWith("jdbc:db2:ids") || url.startsWith("jdbc:informix-sqli:")
                || url.startsWith("jdbc:as400:")){
            return new DB2Dialect();
        }
        return new StandardDialect();
    }

}
