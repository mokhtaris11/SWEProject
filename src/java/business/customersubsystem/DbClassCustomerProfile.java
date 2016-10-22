package business.customersubsystem;

import business.externalinterfaces.CustomerProfile;
import business.externalinterfaces.DbClassCustomerProfileForTest;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Logger;
import middleware.DbConfigProperties;
import middleware.dataaccess.DataAccessSubsystemFacade;
import middleware.exceptions.DatabaseException;
import middleware.externalinterfaces.DataAccessSubsystem;
import middleware.externalinterfaces.DbClass;
import middleware.externalinterfaces.DbConfigKey;

class DbClassCustomerProfile
        implements DbClass, DbClassCustomerProfileForTest {

    private static final Logger LOG
            = Logger.getLogger(DbClassCustomerProfile.class.getName());
    private DataAccessSubsystem dataAccessSS
            = new DataAccessSubsystemFacade();
    private final String READ = "Read";
    private Integer custId;
    String query;
    private String queryType;
    private CustomerProfileImpl customerProfile;

    CustomerProfileImpl getCustomerProfile() {
        return customerProfile;
    }

    @Override
    public void readCustomerProfile(Integer custId)
            throws DatabaseException {
        this.custId = custId;
        queryType = READ;
        dataAccessSS.atomicRead(this);
    }

    @Override
    public void buildQuery() {
        if (queryType.equals(READ)) {
            buildReadQuery();
        }
    }

    void buildReadQuery() {
        query = "SELECT custid,fname,lname "
                + "FROM Customer "
                + "WHERE custid = " + custId;
    }

    @Override
    public void populateEntity(ResultSet resultSet)
            throws DatabaseException {
        try {
            //we take the first returned row
            if (resultSet.next()) {
                customerProfile = new CustomerProfileImpl(resultSet.getInt("custid"),
                        resultSet.getString("fname"),
                        resultSet.getString("lname"));
            } else {
                customerProfile = null;
            }
        } catch (SQLException e) {
            throw new DatabaseException(e);
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

    //FOR TESTING
    @Override
    public CustomerProfile getCustomerProfileForTest() {
        return customerProfile;
    }

}
