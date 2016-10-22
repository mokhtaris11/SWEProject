package middleware.dataaccess;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Logger;
import middleware.exceptions.DatabaseException;
import middleware.externalinterfaces.DbClass;

class DbAction {

    DbAction(DbClass c)
            throws DatabaseException {
        concreteDbClass = c;
        pool = DataAccessUtil.getPool();
        con = pool.getConnection(concreteDbClass.getDbUrl()); //new
    }

    public void rollback()
            throws DatabaseException {
        try {
            con.rollback();
        } catch (SQLException e) {
            throw new DatabaseException("rollback encountered a SQLException " + e.getMessage());
        }
    }

    void performRead()
            throws DatabaseException {
        concreteDbClass.buildQuery();
        resultSet = DataAccessUtil.runQuery(pool, con, concreteDbClass.getQuery());

        concreteDbClass.populateEntity(resultSet);
    }

    Integer performUpdate()
            throws DatabaseException {
        concreteDbClass.buildQuery();
        Integer generatedKey = DataAccessUtil.runUpdate(pool, con, concreteDbClass.getQuery());

        return generatedKey;
    }

    Integer performDelete()
            throws DatabaseException {
        concreteDbClass.buildQuery();
        return DataAccessUtil.runUpdate(pool, con, concreteDbClass.getQuery());
    }

    void returnToPool()
            throws DatabaseException {
        pool.returnToPool(con, concreteDbClass.getDbUrl());
    }

    void startTransaction()
            throws DatabaseException {
        try {
            con.setAutoCommit(false);

        } catch (SQLException e) {
            throw new DatabaseException("DbAction.startTransaction() "
                    + "encountered a SQLException " + e.getMessage());
        }
    }

    void commit()
            throws DatabaseException {
        LOG.info("COMMIT");
        try {
            con.commit();
        } catch (SQLException e) {
            throw new DatabaseException(e.getMessage());
        }
    }
    protected String query;
    protected ResultSet resultSet;
    protected DbClass concreteDbClass;
    protected SimpleConnectionPool pool;
    protected Connection con;
    private static final Logger LOG = Logger.getLogger(DbAction.class.getName());

}
