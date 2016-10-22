package business;

import business.exceptions.BackendException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import middleware.DbConfigProperties;
import middleware.dataaccess.DataAccessSubsystemFacade;
import middleware.exceptions.DatabaseException;
import middleware.externalinterfaces.DataAccessSubsystem;
import middleware.externalinterfaces.DbClass;
import middleware.externalinterfaces.DbConfigKey;

public class DbClassQuantity
        implements DbClass {

    public DbClassQuantity() {
    }

    public void setQuantity(Quantity q) {
        quantity = q;
    }

    /**
     * updates quantity available field in the quantity object
     */
    public void readQuantityAvail(String productName)
            throws BackendException {
        this.productName = productName;
        queryType = READ;
        try {
            dataAccessSS.atomicRead(this);
        } catch (DatabaseException e) {
            throw new BackendException(e);
        }
    }

    @Override
    public void buildQuery() {
        if (queryType.equals(READ)) {
            buildReadQuery();
        }
    }

    @Override
    public void populateEntity(ResultSet rs)
            throws DatabaseException {
        if (queryType.equals(READ)) {
            populateQuantity(rs);
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

    private void buildReadQuery() {
        query = "SELECT totalquantity from Product where productname='" + productName + "'";
    }

    private void populateQuantity(ResultSet rs)
            throws DatabaseException {
        try {
            if (rs.next()) {
                quantity.setQuantityAvailable(rs.getString("totalquantity"));
            }
        } catch (SQLException e) {
            throw new DatabaseException(e);
        }
    }

    /**
     * Quick test
     */
    public static void main(String[] args) {
        DbClassQuantity dbq = new DbClassQuantity();
        Quantity qty = new Quantity("20");
        dbq.setQuantity(qty);
        try {
            dbq.readQuantityAvail("Pants");
            LOG.info(qty.getQuantityAvailable());
        } catch (BackendException e) {
            LOG.log(Level.SEVERE, null, e);
        }
    }

    private final DataAccessSubsystem dataAccessSS = new DataAccessSubsystemFacade();
    private Quantity quantity;
    private String productName;
    private String queryType;
    private String query;
    private final String READ = "Read";
    private final String QUANTITY_AVAIL = "totalquantity";
    private static final Logger LOG = Logger.getLogger(DbClassQuantity.class.getName());

}
