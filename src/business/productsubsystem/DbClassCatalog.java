package business.productsubsystem;

import business.exceptions.BackendException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import middleware.DbConfigProperties;
import middleware.dataaccess.DataAccessSubsystemFacade;
import middleware.dataaccess.DataAccessUtil;
import middleware.exceptions.DatabaseException;
import middleware.externalinterfaces.DataAccessSubsystem;
import middleware.externalinterfaces.DbClass;
import middleware.externalinterfaces.DbConfigKey;

public class DbClassCatalog
        implements DbClass {

    private static final Logger LOG
            = Logger.getLogger(DbClassCatalog.class.getName());
    private DataAccessSubsystem dataAccessSS
            = new DataAccessSubsystemFacade();

    private String catalogName;
    private int catalogId;
    private String query;
    private String queryType;
    private final String SAVE = "Save";
    private final String DELETE = "Delete";
    private final String EDIT = "Edit";

    public void saveNewCatalog(String name)
            throws BackendException {
        this.catalogName = name;
        queryType = SAVE;

        try {
            dataAccessSS.saveWithinTransaction(this);
        } catch (DatabaseException e) {
            LOG.log(Level.WARNING, "A DatabaseException was thrown: {0}", e.getMessage());
            throw new BackendException(e);
        }
    }

    public void saveEditCatalog(int catalogId, String name)
            throws BackendException {
        this.catalogName = name;
        this.catalogId = catalogId;
        queryType = EDIT;

        try {
            dataAccessSS.saveWithinTransaction(this);
        } catch (DatabaseException e) {
            LOG.log(Level.WARNING, "A DatabaseException was thrown: {0}", e.getMessage());
            throw new BackendException(e);
        }
    }

    public void deleteCatalog(String name)
            throws BackendException {
        this.catalogName = name;
        deleteCatalog();
    }

    public void deleteCatalog(int catalogId)
            throws BackendException {
        this.catalogId = catalogId;
        deleteCatalog();
    }

    private void deleteCatalog()
            throws BackendException {
        queryType = DELETE;
        try {
            dataAccessSS.deleteWithinTransaction(this);
        } catch (DatabaseException e) {
            LOG.log(Level.WARNING, "A DatabaseException was thrown: {0}", e.getMessage());
            throw new BackendException(e);
        }
    }

    @Override
    public void buildQuery()
            throws DatabaseException {
        if (queryType.equals(SAVE)) {
            buildSaveQuery();
        } else if (queryType.equals(DELETE)) {
            buildDeleteQuery();
        } else if (queryType.equals(EDIT)) {
            buildEditQuery();
        }
    }

    void buildSaveQuery()
            throws DatabaseException {
        query = "INSERT into CatalogType "
                + "(catalogid,catalogname) "
                + "VALUES(NULL,'"
                + catalogName + "')";
    }

    void buildDeleteQuery()
            throws DatabaseException {
        query = "DELETE FROM CatalogType WHERE CatalogName='" + catalogName + "'";
    }

    void buildEditQuery()
            throws DatabaseException {
        query = "UPDATE CatalogType SET CatalogName='" + catalogName + "' WHERE CatalogId =" + catalogId;
    }

    @Override
    public String getDbUrl() {
        DbConfigProperties props = new DbConfigProperties();
        return props.getProperty(DbConfigKey.PRODUCT_DB_URL.getVal());
    }

    @Override
    public String getQuery() {
        return query;
    }

    @Override
    public void populateEntity(ResultSet resultSet)
            throws DatabaseException {

    }

}
