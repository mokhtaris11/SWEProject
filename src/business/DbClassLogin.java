package business;

import business.exceptions.BackendException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Logger;
import middleware.DbConfigProperties;
import middleware.dataaccess.DataAccessSubsystemFacade;
import middleware.exceptions.DatabaseException;
import middleware.externalinterfaces.DataAccessSubsystem;
import middleware.externalinterfaces.DbClass;
import middleware.externalinterfaces.DbConfigKey;

public class DbClassLogin
        implements DbClass {

    public DbClassLogin(Login login) {
        this.login = login;
        this.custId = login.getCustId();
        this.password = login.getPassword();
    }

    public boolean authenticate()
            throws BackendException {
        try {
            dataAccessSS.atomicRead(this);
        } catch (DatabaseException e) {
            throw new BackendException(e);
        }

        return authenticated;
    }

    public int getAuthorizationLevel() {
        return authorizationLevel;
    }

    /**
     * Tries to locate the custId/password pair in Customer table
     */
    @Override
    public void buildQuery() {
        query = "SELECT * FROM Customer WHERE custid = " + custId + " AND password = '" + password + "'";
    }

    @Override
    public void populateEntity(ResultSet resultSet)
            throws DatabaseException {
        try {
            if (resultSet.next()) {
                authorizationLevel = 1;//resultSet.getInt("admin");
                authenticated = true;
            }

        } catch (SQLException e) {
            throw new DatabaseException(e.getMessage());
        }
    }

    @Override
    public String getDbUrl() {
        DbConfigProperties props = new DbConfigProperties();
        return props.getProperty(DbConfigKey.ACCOUNT_DB_URL.getVal());
    }

    @Override
    public String getQuery() {
        return query;
    }

    private final DataAccessSubsystem dataAccessSS = new DataAccessSubsystemFacade();
    private final Integer custId;
    private final String password;
    @SuppressWarnings("unused")
    private final Login login;
    private String query;
    private DataAccessSubsystem dataAccess;
    private boolean authenticated;
    int authorizationLevel;
    private static final Logger LOG = Logger.getLogger(DbClassLogin.class.getName());
}
