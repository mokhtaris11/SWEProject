package business.customersubsystem;

import business.exceptions.BackendException;
import business.exceptions.BusinessException;
import business.exceptions.RuleException;
import business.externalinterfaces.Address;
import business.externalinterfaces.CartItem;
import business.externalinterfaces.CreditCard;
import business.externalinterfaces.CreditCardForTest;
import business.externalinterfaces.CustomerProfile;
import business.externalinterfaces.CustomerSubsystem;
import business.externalinterfaces.DbClassAddressForTest;
import business.externalinterfaces.DbClassCustomerProfileForTest;
import business.externalinterfaces.Order;
import business.externalinterfaces.OrderSubsystem;
import business.externalinterfaces.Rules;
import business.externalinterfaces.ShoppingCart;
import business.externalinterfaces.ShoppingCartSubsystem;
import business.ordersubsystem.OrderSubsystemFacade;
import business.shoppingcartsubsystem.ShoppingCartSubsystemFacade;
import business.util.OrderUtil;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import middleware.creditverification.CreditVerificationFacade;
import middleware.exceptions.DatabaseException;
import middleware.exceptions.MiddlewareException;
import middleware.externalinterfaces.CreditVerification;

public class CustomerSubsystemFacade
        implements CustomerSubsystem {

    @Override
    public void initializeCustomer(Integer id, int authorizationLevel)
            throws BackendException {

        try {
            isAdmin = (authorizationLevel >= 1);
            loadCustomerProfile(id);
            loadDefaultShipAddress();
            loadDefaultBillAddress();
            loadDefaultPaymentInfo();
            shoppingCartSubsystem = new ShoppingCartSubsystemFacade();
            shoppingCartSubsystem.setCustomerProfile(customerProfile);
            shoppingCartSubsystem.retrieveSavedCart();
            orderSubsystem = new OrderSubsystemFacade(this);
            loadOrderData();
        } catch (DatabaseException e) {
            throw new BackendException(e);
        }
    }

    @Override
    public boolean isAdmin() {
        return isAdmin;
    }

    @Override
    public void refreshAfterSubmit()
            throws BackendException {
        loadOrderData();
    }

    // /implementation of interface
    /**
     * Return an (unmodifiable) copy of the order history.
     */
    @Override
    public List<Order> getOrderHistory() {
        return Collections.unmodifiableList(orderHistory);
    }

    /*@Override
     public List<Order> getOrderHistory(int customerId) {
     return Collections.unmodifiableList(orderHistory);
     }*/
    @Override
    public void saveNewAddress(Address addr)
            throws BackendException {
        try {
            DbClassAddress dbClass = new DbClassAddress();
            dbClass.setAddress(addr);
            dbClass.saveAddress(customerProfile);
        } catch (DatabaseException e) {
            throw new BackendException(e);
        }
    }

    /**
     * Return an (unmodifiable) copy of the addresses
     */
    @Override
    public List<Address> getAllAddresses()
            throws BackendException {
        try {
            DbClassAddress dbClass = new DbClassAddress();
            dbClass.readAllAddresses(customerProfile);
            return Collections.unmodifiableList(dbClass.getAddressList());
        } catch (DatabaseException e) {
            throw new BackendException(e);
        }
    }

    @Override
    public CustomerProfile getCustomerProfile() {
        return customerProfile;
    }

    @Override
    public Address getDefaultShippingAddress() {
        return defaultShipAddress;
    }

    @Override
    public Address getDefaultBillingAddress() {
        return defaultBillAddress;
    }

    @Override
    public void setShippingAddressInCart(Address addr) {
        shoppingCartSubsystem.setShippingAddress(addr);

    }

    @Override
    public void setBillingAddressInCart(Address addr) {
        shoppingCartSubsystem.setBillingAddress(addr);

    }

    @Override
    public CreditCard getDefaultPaymentInfo() {
        return defaultPaymentInfo;
    }

    @Override
    public void setPaymentInfoInCart(CreditCard cc) {
        shoppingCartSubsystem.setPaymentInfo(cc);
    }

    /**
     * This method inserts the liveCart of customer's shopping cart subsystem into the order subsystem on its
     * way to the database, in the form of a to-be-processed order.
     */
    @Override
    public void submitOrder()
            throws BackendException {
        orderSubsystem.submitOrder(shoppingCartSubsystem.getLiveCart());
    }

    @Override
    public void saveShoppingCart()
            throws BackendException {

        //insert default values for address and payment
        if (shoppingCartSubsystem.getLiveCart().getShippingAddress() == null) {
            shoppingCartSubsystem.setShippingAddress(defaultShipAddress);
        }
        if (shoppingCartSubsystem.getLiveCart().getBillingAddress() == null) {
            shoppingCartSubsystem.setBillingAddress(defaultBillAddress);
        }
        if (shoppingCartSubsystem.getLiveCart().getPaymentInfo() == null) {
            shoppingCartSubsystem.setPaymentInfo(this.defaultPaymentInfo);
        }
        shoppingCartSubsystem.saveLiveCart();
    }

    @Override
    public Address runAddressRules(Address addr)
            throws RuleException, BusinessException {
        Rules transferObject = new RulesAddress(addr);
        transferObject.runRules();

        // updates are in the form of a List; 0th object is the necessary
        // Address
        AddressImpl update = (AddressImpl) transferObject.getUpdates().get(0);
        return update;
    }

    @Override
    public void runPaymentRules(Address addr, CreditCard cc)
            throws RuleException, BusinessException {
        // Implement (Done)
        Rules transferObject = new RulesPayment(addr, cc);
        transferObject.runRules();
    }

    @Override
    public ShoppingCartSubsystem getShoppingCart() {
        return shoppingCartSubsystem;
    }

    /**
     * Customer Subsystem is responsible for obtaining all the data needed by Credit Verif system -- it does
     * not (and should not) rely on the controller for this data.
     */
    @Override
    public void checkCreditCard()
            throws BusinessException {
        List<CartItem> items = shoppingCartSubsystem
                .getLiveCartItems();
        ShoppingCart theCart = shoppingCartSubsystem
                .getLiveCart();
        Address billAddr = theCart.getBillingAddress();
        CreditCard cc = theCart.getPaymentInfo();
        String amount = OrderUtil.quickComputeTotalPrice(items);
        CreditVerification creditVerif = new CreditVerificationFacade();
        try {
            creditVerif.checkCreditCard(customerProfile, billAddr, cc,
                    new Double(amount));
        } catch (MiddlewareException e) {
            throw new BusinessException(e);
        }
    }

    //FOR TESTING
    @Override
    public DbClassAddressForTest getGenericDbClassAddress() {
        return new DbClassAddress();
    }

    @Override
    public CustomerProfile getGenericCustomerProfile() {
        return new CustomerProfileImpl(1, "FirstTest", "LastTest");
    }

    @Override
    public DbClassCustomerProfileForTest getGenericDbClassCustomerProfile() {
        return new DbClassCustomerProfile();
    }

    @Override
    public CreditCardForTest createCreditCardForTest(String cardNumber, String cardType, String expireDate, String nameOnCard) {
        CreditCardForTest cc = new CreditCardImpl(cardNumber, cardType, expireDate, nameOnCard);

        return cc;
    }

    @Override
    public void setShoppingCart(ShoppingCartSubsystem shoppingCart) {
        shoppingCartSubsystem = shoppingCart;
    }

    void loadCustomerProfile(Integer custId)
            throws DatabaseException {
        DbClassCustomerProfile dbclass = new DbClassCustomerProfile();
        dbclass.readCustomerProfile(custId);
        customerProfile = dbclass.getCustomerProfile();
    }

    void loadDefaultShipAddress()
            throws DatabaseException {
        DbClassAddress dbclass = new DbClassAddress();
        dbclass.readDefaultShipAddress(customerProfile);
        defaultShipAddress = dbclass.getDefaultShipAddress();
    }

    void loadDefaultBillAddress()
            throws DatabaseException {
        DbClassAddress dbclass = new DbClassAddress();
        dbclass.readDefaultBillAddress(customerProfile);
        defaultBillAddress = dbclass.getDefaultBillAddress();
    }

    void loadDefaultPaymentInfo()
            throws DatabaseException {
        DbClassCreditCard dbclass = new DbClassCreditCard();
        dbclass.readDefaultPaymentInfo(customerProfile);
        defaultPaymentInfo = dbclass.getDefaultPaymentInfo();
    }

    void loadOrderData()
            throws BackendException {
        // Implement (Done)
        orderHistory = orderSubsystem.getOrderHistory();
        LOG.log(Level.INFO, "Order history count {0}", orderHistory.size());
    }

    public static Address createAddress(String street, String city, String state, String zip) {
        return new AddressImpl(street, city, state, zip);
    }

    public static CreditCard createCreditCard(String name, String num, String type, String expDate) {

        return new CreditCardImpl(name, expDate, num, type);
    }

    // assumes array is in the form street,city,state,zip
    public static Address createAddress(String[] addressInfo) {

        return createAddress(addressInfo[0], addressInfo[1], addressInfo[2],
                addressInfo[3]);
    }
    ShoppingCartSubsystem shoppingCartSubsystem;
    OrderSubsystem orderSubsystem;
    List<Order> orderHistory;
    AddressImpl defaultShipAddress;
    AddressImpl defaultBillAddress;
    CreditCardImpl defaultPaymentInfo;
    CustomerProfileImpl customerProfile;
    boolean isAdmin;
    private static final Logger LOG = Logger.getLogger(CustomerSubsystemFacade.class.getName());

}
