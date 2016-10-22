package business.customersubsystem;

import business.externalinterfaces.CreditCard;
import business.externalinterfaces.CustomerProfile;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;
import middleware.DbConfigProperties;
import middleware.dataaccess.DataAccessSubsystemFacade;
import middleware.exceptions.DatabaseException;
import middleware.externalinterfaces.DataAccessSubsystem;
import middleware.externalinterfaces.DbClass;
import middleware.externalinterfaces.DbConfigKey;

class DbClassCreditCard
        implements DbClass {

    public void savePayment(CustomerProfile custProfile)
            throws DatabaseException {
        this.custProfile = custProfile;
        queryType = SAVE;
        dataAccessSS.saveWithinTransaction(this);
    }

    @Override
    public void buildQuery() {
        if (queryType.equals(SAVE)) {
            buildSaveNewPaymentQuery();
        } else if (queryType.equals(READ)) {
            buildReadAllPaymentsQuery();
        } else if (queryType.equals(READ_DEFAULT_PAYMENT)) {
            buildReadDefaultPaymentQuery();
        }
    }

    public List<CreditCard> getPaymentList() {
        return paymentList;
    }

    public void readDefaultPaymentInfo(CustomerProfile custProfile)
            throws DatabaseException {
        this.custProfile = custProfile;
        queryType = READ_DEFAULT_PAYMENT;
        dataAccessSS.atomicRead(this);
    }

    public void readAllPayments(CustomerProfile custProfile)
            throws DatabaseException {
        this.custProfile = custProfile;
        queryType = READ;
        dataAccessSS.atomicRead(this);
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

    @Override
    public void populateEntity(ResultSet resultSet)
            throws DatabaseException {
        if (queryType.equals(READ)) {
            populatePaymentList(resultSet);
        } else if (queryType.equals(READ_DEFAULT_PAYMENT)) {
            populateDefaultPayment(resultSet);
        }
    }

    CreditCard getPayment() {
        return payment;
    }

    CreditCardImpl getDefaultPaymentInfo() {
        return this.defaultPaymentInfo;
    }

    void setCreditCard(CreditCard payment) {
        this.payment = payment;
    }

    void buildSaveNewPaymentQuery() {
        query = "INSERT into altpayment "
                + "(paymentid,custid," + EXP_DATE + "," + CARD_TYPE + "," + CARD_NUM + ") "
                + "VALUES(NULL,"
                + custProfile.getCustId() + ",'"
                + payment.getExpirationDate() + "','"
                + payment.getCardType() + "','"
                + payment.getCardNum() + "')";
    }

    void buildReadAllPaymentsQuery() {
        query = "SELECT " + EXP_DATE + ", " + CARD_TYPE + ", " + CARD_NUM + " from customer";
    }

    void buildReadDefaultPaymentQuery() {
        query = "SELECT " + EXP_DATE + ", " + CARD_TYPE + ", " + CARD_NUM + " FROM customer WHERE custid = " + custProfile.getCustId();
    }

    void populatePaymentList(ResultSet rs)
            throws DatabaseException {
        paymentList = new LinkedList<CreditCard>();
        if (rs != null) {
            try {
                while (rs.next()) {
                    payment = new CreditCardImpl();
                    payment.setExpirationDate(rs.getString(EXP_DATE));
                    payment.setCardType(rs.getString(CARD_TYPE));
                    payment.setCardNum(rs.getString(CARD_NUM));
                    paymentList.add(payment);
                }
            } catch (SQLException e) {
                throw new DatabaseException(e);
            }
        }
    }

    void populateDefaultPayment(ResultSet rs)
            throws DatabaseException {
        try {
            if (rs.next()) {
                defaultPaymentInfo = new CreditCardImpl(this.custProfile.getFirstName(),
                        rs.getString(EXP_DATE),
                        rs.getString(CARD_NUM),
                        rs.getString(CARD_TYPE));
            }
        } catch (SQLException e) {
            throw new DatabaseException(e);
        }
    }
    private DataAccessSubsystem dataAccessSS = new DataAccessSubsystemFacade();
    private CustomerProfile custProfile;
    private CreditCard payment;
    private List<CreditCard> paymentList;
    private CreditCardImpl defaultPaymentInfo;
    private String queryType;
    private String query;

    private final String SAVE = "Save";
    private final String READ = "Read";
    private final String READ_DEFAULT_PAYMENT = "ReadDefaultPayment";

    //column names
    private final String EXP_DATE = "expdate";
    private final String CARD_TYPE = "cardtype";
    private final String CARD_NUM = "cardnum";
    private static final Logger LOG = Logger.getLogger(DbClassCreditCard.class.getName());
}
