package business.productsubsystem;

import business.exceptions.BackendException;
import business.externalinterfaces.DbClassCatalogTypesForTest;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Logger;

import middleware.DbConfigProperties;
import middleware.dataaccess.DataAccessSubsystemFacade;
import middleware.exceptions.DatabaseException;
import middleware.externalinterfaces.DataAccessSubsystem;
import middleware.externalinterfaces.DbClass;
import middleware.externalinterfaces.DbConfigKey;

import business.externalinterfaces.CatalogTypes;
import java.util.logging.Level;

/**
 *  pcorazza
 * <p>
 * Class Description:
 */
public class DbClassCatalogTypes
        implements DbClass, DbClassCatalogTypesForTest {

    private static final Logger LOG
            = Logger.getLogger(DbClassCatalogTypes.class.getName());
    private DataAccessSubsystem dataAccessSS
            = new DataAccessSubsystemFacade();
    private String query;
    private String queryType;
    final String GET_TYPES = "GetTypes";
    private CatalogTypes types;

    @Override
    public CatalogTypes getCatalogTypes()
            throws BackendException {
        queryType = GET_TYPES;
        try {
            dataAccessSS.atomicRead(this);
        } catch (DatabaseException e) {
            LOG.log(Level.WARNING, "A DatabaseException was thrown: {0}", e.getMessage());
            throw new BackendException(e);
        }
        return types;
    }

    @Override
    public void buildQuery() {
        if (queryType.equals(GET_TYPES)) {
            buildGetTypesQuery();
        }
    }

    void buildGetTypesQuery() {
        query = "SELECT * FROM CatalogType";
    }

    /**
     * This is activated when getting all catalog types.
     */
    @Override
    public void populateEntity(ResultSet resultSet)
            throws DatabaseException {
        types = new CatalogTypesImpl();
        try {
            while (resultSet.next()) {
                types.addCatalog(resultSet.getInt("catalogid"),
                        resultSet.getString("catalogname"));
            }
        } catch (SQLException e) {
            LOG.log(Level.WARNING, "A SQLException was thrown: {0}", e.getMessage());
            throw new DatabaseException(e);
        }

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

}
