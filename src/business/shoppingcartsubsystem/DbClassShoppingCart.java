package business.shoppingcartsubsystem;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

import static business.util.StringParse.*;

import business.*;
import business.customersubsystem.CustomerSubsystemFacade;
import business.exceptions.BackendException;
import business.externalinterfaces.Address;
import business.externalinterfaces.CartItem;
import business.externalinterfaces.CreditCard;
import business.externalinterfaces.CustomerProfile;
import business.externalinterfaces.CustomerSubsystem;
import business.externalinterfaces.DbClassShoppingCartForTest;
import business.externalinterfaces.OrderItem;
import business.externalinterfaces.ShoppingCart;
import business.util.OrderUtil;
import java.util.logging.Level;
import middleware.DbConfigProperties;
import middleware.dataaccess.DataAccessSubsystemFacade;
import middleware.exceptions.DatabaseException;
import middleware.externalinterfaces.DataAccessSubsystem;
import middleware.externalinterfaces.DbClass;
import middleware.externalinterfaces.DbConfigKey;

public class DbClassShoppingCart
        implements DbClass, DbClassShoppingCartForTest {

    private static final Logger LOG = Logger.getLogger(DbClassShoppingCart.class
            .getPackage().getName());
    private DataAccessSubsystem dataAccessSS = new DataAccessSubsystemFacade();
    DataAccessSubsystem dataAccess;
    ShoppingCart cart;
    CartItem cartItem;
    List<CartItem> cartItemsList;
    CustomerProfile custProfile;
    Integer cartId;
    String query;
    final String GET_ID = "GetId";
    final String GET_SAVED_ITEMS = "GetSavedItems";
    final String GET_TOP_LEVEL_SAVED_CART = "GetTopLevelSavedCart";
    final String SAVE_CART = "SaveCart";
    final String SAVE_CART_ITEM = "SaveCartItem";
    final String DELETE_CART = "DeleteCart";
    final String DELETE_ALL_CART_ITEMS = "DeleteAllCartItems";
    String queryType;

    @Override
    public void buildQuery() {
        if (queryType.equals(GET_ID)) {
            buildGetIdQuery();
        } else if (queryType.equals(GET_SAVED_ITEMS)) {
            buildGetSavedItemsQuery();
        } else if (queryType.equals(GET_TOP_LEVEL_SAVED_CART)) {
            this.buildGetTopLevelCartQuery();
        } else if (queryType.equals(SAVE_CART)) {
            buildSaveCartQuery();
        } else if (queryType.equals(SAVE_CART_ITEM)) {
            buildSaveCartItemQuery();
        } else if (queryType.equals(DELETE_CART)) {
            buildDeleteCartQuery();
        } else if (queryType.equals(DELETE_ALL_CART_ITEMS)) {
            buildDeleteAllCartItemsQuery();
        }
    }

    private void buildGetIdQuery() {
        query = "SELECT shopcartid "
                + "FROM ShopCartTbl "
                + "WHERE custid = " + custProfile.getCustId();
    }

    private void buildDeleteCartQuery() {
        query = "DELETE FROM shopcarttbl WHERE shopcartid = " + cartId;
    }

    private void buildDeleteAllCartItemsQuery() {
        query = "DELETE FROM shopcartitem WHERE shopcartid = " + cartId;
    }

    //precondition: cart and custprofile has been stored as instance variable
    private void buildSaveCartQuery() {
        Address shipAddress = cart.getShippingAddress();
        Address billAddress = cart.getBillingAddress();
        CreditCard paymentInfo = cart.getPaymentInfo();
        query = "INSERT INTO shopcarttbl (shopcartid, custId, shipaddress1, shipaddress2, shipcity, shipstate, shipzipcode,"
                + " billaddress1, billaddress2, billcity, billstate, billzipcode, nameoncard, expdate, cardtype, cardnum,"
                + " totalpriceamount, totalshipmentcost, totaltaxamount, totalamountcharged)"
                + " VALUES (NULL, " + custProfile.getCustId() + ", '"
                + shipAddress.getStreet1() + "', '" + shipAddress.getStreet2() + "', '"
                + shipAddress.getCity() + "', '" + shipAddress.getState() + "', '" + shipAddress.getZip() + "', '"
                + billAddress.getStreet1() + "', '" + billAddress.getStreet2() + "', '"
                + billAddress.getCity() + "', '" + billAddress.getState() + "', '" + billAddress.getZip() + "', '"
                + paymentInfo.getNameOnCard() + "', '" + paymentInfo.getExpirationDate() + "', '" + paymentInfo.getCardType() + "', '" + paymentInfo.getCardNum() + "', "
                + cart.getTotalPrice() + ", 0, 0, " + cart.getTotalPrice() + ")";
    }

    private void buildSaveCartItemQuery() {
        query = "INSERT INTO shopcartitem (cartitemid, shopcartid, productid, quantity, totalprice, shipmentcost, taxamount) "
                + "VALUES (NULL, " + cartItem.getCartId() + ", " + cartItem.getProductId() + ", '" + cartItem.getQuantity() + "', '"
                + cartItem.getTotalPrice() + "', '0','0')";
    }

    private void buildGetSavedItemsQuery() {
        query = "SELECT * FROM shopcartitem WHERE shopcartid = " + cartId;
    }

    private void buildGetTopLevelCartQuery() {
        query = "SELECT * FROM shopcarttbl WHERE shopcartid = " + cartId;

    }

    public void deleteCart(String cartId)
            throws BackendException {
        this.cartId = Integer.parseInt(cartId);
        queryType = DELETE_CART;
        try {
            dataAccessSS.deleteWithinTransaction(this);
        } catch (DatabaseException e) {
            LOG.log(Level.WARNING, "A DatabaseException was thrown: {0}", e.getMessage());
            throw new BackendException(e);
        }
    }

    //Support method for saveCart -- part of another transaction
    private void deleteCart(Integer cartId)
            throws DatabaseException {
        this.cartId = cartId;
        queryType = DELETE_CART;
        dataAccessSS.delete();
    }

    public void deleteAllCartItems(String cartId)
            throws BackendException {
        this.cartId = Integer.parseInt(cartId);
        queryType = DELETE_ALL_CART_ITEMS;
        try {
            dataAccessSS.deleteWithinTransaction(this);
        } catch (DatabaseException e) {
            LOG.log(Level.WARNING, "A DatabaseException was thrown: {0}", e.getMessage());
            throw new BackendException(e);
        }
    }

    //Support method for saveCart -- part of another transaction
    private void deleteAllCartItems(Integer cartId)
            throws DatabaseException {
        this.cartId = cartId;
        queryType = DELETE_ALL_CART_ITEMS;
        dataAccessSS.delete();
    }

    @Override
    public ShoppingCart retrieveSavedCart(CustomerProfile custProfile)
            throws BackendException {
        try {
            dataAccessSS.createConnection(this);
            getShoppingCartId(custProfile);
            if (cartId != null) {
                getTopLevelSavedCart(cartId);
                if (cart != null) {
                    cart.setCartItems(getSavedCartItems(cartId));
                }
            }
            dataAccessSS.releaseConnection();
        } catch (DatabaseException e) {
            LOG.log(Level.WARNING, "A DatabaseException was thrown: {0}", e.getMessage());
            throw new BackendException(e);
        }
        return cart;
    }

    //support method for retrieveSavedCart
    private Integer getShoppingCartId(CustomerProfile custProfile)
            throws DatabaseException {
        this.custProfile = custProfile;
        queryType = GET_ID;
        dataAccessSS.read();
        return cartId;
    }

    //support method for retrieveSavedCart
    private List<CartItem> getSavedCartItems(Integer cartId)
            throws DatabaseException {
        this.cartId = cartId;
        queryType = GET_SAVED_ITEMS;
        dataAccessSS.read();
        return cartItemsList;

    }

    //support method for retrieveSavedCart
    private ShoppingCart getTopLevelSavedCart(Integer cartId)
            throws DatabaseException {
        this.cartId = cartId;
        queryType = GET_TOP_LEVEL_SAVED_CART;
        dataAccessSS.read();
        return cart;
    }

    public void saveCart(CustomerProfile custProfile, ShoppingCart cart)
            throws BackendException {
        this.custProfile = custProfile;
        this.cart = cart;

        try {

            dataAccessSS.createConnection(this);
            dataAccessSS.startTransaction();

            try {
                // read saved cart id
                this.cartId = getShoppingCartId(custProfile);

                if (this.cartId != null) {
                    // delete saved cart items
                    deleteAllCartItems(this.cartId);

                    // delete saved cart
                    deleteCart(this.cartId);
                }

                // save live cart
                queryType = SAVE_CART;
                this.cartId = dataAccessSS.save();

                // save live cart items
                queryType = SAVE_CART_ITEM;
                for (CartItem item : cart.getCartItems()) {
                    item.setCartId(this.cartId);
                    this.cartItem = item;
                    dataAccessSS.save();
                }

                dataAccessSS.commit();
            } catch (DatabaseException e) {
                LOG.warning("Attempting to rollback...");
                dataAccessSS.rollback();
                throw (e);
            } finally {
                dataAccessSS.releaseConnection();
            }
        } catch (DatabaseException e) {
            LOG.log(Level.WARNING, "A DatabaseException was thrown: {0}", e.getMessage());
            throw new BackendException(e);
        }
    }

    @Override
    public void populateEntity(ResultSet resultSet)
            throws DatabaseException {
        if (queryType.equals(GET_ID)) {
            populateShopCartId(resultSet);
        } else if (queryType.equals(GET_SAVED_ITEMS)) {
            populateCartItemsList(resultSet);
        } else if (queryType.equals(GET_TOP_LEVEL_SAVED_CART)) {
            populateTopLevelCart(resultSet);
        }

    }

    private void populateShopCartId(ResultSet rs) {
        try {
            if (rs.next()) {
                cartId = rs.getInt("shopcartid");
            }
        } catch (SQLException e) {
            LOG.log(Level.WARNING, "A SQLException was thrown: {0}", e.getMessage());
            //do nothing
        }
    }

    private void populateTopLevelCart(ResultSet rs)
            throws DatabaseException {
        cart = new ShoppingCartImpl();
        Address shippingAddress = null;
        Address billingAddress = null;
        CreditCard creditCard = null;
        try {
            if (rs.next()) {
                //load shipping address
                String shipStreet = rs.getString("shipaddress1");
                String shipCity = rs.getString("shipcity");
                String shipState = rs.getString("shipstate");
                String shipZip = rs.getString("shipzipcode");
                shippingAddress
                        = CustomerSubsystemFacade.createAddress(shipStreet, shipCity, shipState, shipZip);

                //load billing address
                String billStreet = rs.getString("billaddress1");
                String billCity = rs.getString("billcity");
                String billState = rs.getString("billstate");
                String billZip = rs.getString("billzipcode");
                billingAddress
                        = CustomerSubsystemFacade.createAddress(billStreet, billCity, billState, billZip);

                //load credit card: createCreditCard(String name, String num, String type, expDate)
                String name = rs.getString("nameoncard");
                String num = rs.getString("cardnum");
                String type = rs.getString("cardtype");
                String exp = rs.getString("expdate");
                creditCard
                        = CustomerSubsystemFacade.createCreditCard(name, num, type, exp);

                //load cart
                cart.setCartId(rs.getString("shopcartid"));
                cart.setShipAddress(shippingAddress);
                cart.setBillAddress(billingAddress);
                cart.setPaymentInfo(creditCard);

            }
        } catch (SQLException e) {
            LOG.log(Level.WARNING, "A SQLException was thrown: {0}", e.getMessage());
            throw new DatabaseException(e);
        }

    }

    private void populateCartItemsList(ResultSet rs)
            throws DatabaseException {
        CartItem cartItem = null;
        cartItemsList = new LinkedList<CartItem>();
        try {
            while (rs.next()) {
                cartItem = new CartItemImpl(
                        rs.getInt("cartitemid"), rs.getInt("shopcartid"),
                        rs.getInt("productid"),
                        makeString(rs.getInt("quantity")),
                        makeString(rs.getDouble("totalprice")),
                        true);

                cartItemsList.add(cartItem);
            }
        } catch (SQLException e) {
            LOG.log(Level.WARNING, "A SQLException was thrown: {0}", e.getMessage());
            throw new DatabaseException(e);
        } catch (BackendException ex) {
            LOG.log(Level.WARNING, "A BackendException was thrown: {0}", ex.getMessage());
            throw new DatabaseException(ex);
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

    public static void main(String[] args) {
        /*
         DbClassShoppingCart dbclass = new DbClassShoppingCart();
         CustomerSubsystemFacade c = new CustomerSubsystemFacade();
         CustomerProfile profile = c.createCustomerProfile(1);

         dbclass.buildSaveCartQuery();
         String saveQuery = dbclass.getQuery();
         dbclass.buildSaveCartItemQuery();
         String saveCartItemQuery = dbclass.getQuery();
         LOG.info("Save cart query: \n");
         LOG.info(saveQuery);
         LOG.info("\nSave cart item query: \n");
         LOG.info(saveCartItemQuery);*/
    }
}
