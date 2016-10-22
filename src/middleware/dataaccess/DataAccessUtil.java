package middleware.dataaccess;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import middleware.DbConfigProperties;
import middleware.exceptions.DatabaseException;
import middleware.externalinterfaces.DbConfigKey;

/**
 *  pcorazza
 * @since Nov 10, 2004 Class Description:
 *
 *
 */
public class DataAccessUtil {

    static ResultSet runQuery(SimpleConnectionPool pool, Connection con, String query)
            throws DatabaseException {
        LOG.log(Level.INFO, "Executing query: {0}", query);
        ResultSet rs = SimpleConnectionPool.doQuery(con, query);

        return rs;
    }

    static Integer runUpdate(SimpleConnectionPool pool, Connection con, String query)
            throws DatabaseException {
        Integer generatedKey = SimpleConnectionPool.doUpdate(con, query);

        return generatedKey;
    }

    static SimpleConnectionPool getPool()
            throws DatabaseException {

        DbConfigProperties props = new DbConfigProperties();
        return SimpleConnectionPool.getInstance(
                props.getProperty(DbConfigKey.DB_USER.getVal()),
                props.getProperty(DbConfigKey.DB_PASSWORD.getVal()),
                props.getProperty(DbConfigKey.DRIVER.getVal()),
                Integer.parseInt(props.getProperty(DbConfigKey.MAX_CONNECTIONS.getVal())));

    }

    private static final Logger LOG = Logger.getLogger(DataAccessUtil.class.getName());

}
