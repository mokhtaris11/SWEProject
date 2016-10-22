package business.ordersubsystem;

import business.exceptions.BackendException;
import business.externalinterfaces.Address;
import business.externalinterfaces.CartItem;
import business.externalinterfaces.CreditCard;
import business.externalinterfaces.CustomerProfile;
import business.externalinterfaces.DbClassOrderForTest;
import business.externalinterfaces.Order;
import business.externalinterfaces.OrderItem;
import business.externalinterfaces.ShoppingCart;
import business.util.OrderUtil;
import static business.util.StringParse.makeString;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import middleware.DbConfigProperties;
import middleware.dataaccess.DataAccessSubsystemFacade;
import middleware.exceptions.DatabaseException;
import middleware.externalinterfaces.DataAccessSubsystem;
import middleware.externalinterfaces.DbClass;
import middleware.externalinterfaces.DbConfigKey;

class DbClassOrder
        implements DbClass, DbClassOrderForTest {

    DbClassOrder() {
    }

    DbClassOrder(OrderImpl order) {
        this.order = order;
    }

    DbClassOrder(OrderItem orderItem) {
        this.orderItem = orderItem;
    }

    DbClassOrder(CustomerProfile custProfile) {
        this.custProfile = custProfile;
    }

    DbClassOrder(OrderImpl order, CustomerProfile custProfile) {
        this(order);
        this.custProfile = custProfile;
    }

    public List<Order> getAllOrders()
            throws BackendException {
        try {
            queryType = GET_ALL_ORDERS;
            dataAccessSS.atomicRead(this);
            return Collections.unmodifiableList(allOrders);
        } catch (DatabaseException ex) {
            LOG.log(Level.WARNING, "A DatabaseException was thrown: {0}", ex.getMessage());
            throw new BackendException(ex);
        }
    }

    @Override
    public Integer addOrderItemsForTest(ShoppingCart shopCart, CustomerProfile custProfile)
            throws BackendException {
        this.custProfile = custProfile;
        submitOrder(shopCart);

        return orderId;
    }

    @Override
    public void deleteOrder(int orderId)
            throws BackendException {
        try {
            deleteOrderItem(orderId);
            queryType = DELETE_ORDER;
            this.orderId = orderId;
            dataAccessSS.deleteWithinTransaction(this);
        } catch (DatabaseException ex) {
            LOG.log(Level.WARNING, "A DatabaseException was thrown: {0}", ex.getMessage());
            throw new BackendException(ex);
        }
    }

    @Override
    public List<OrderItem> getOrderItems(Integer orderId)
            throws BackendException {
        try {
            queryType = GET_ORDER_ITEMS;
            this.orderId = orderId;
            dataAccessSS.atomicRead(this);
            return Collections.unmodifiableList(orderItems);
        } catch (DatabaseException e) {
            LOG.log(Level.WARNING, "A DatabaseException was thrown: {0}", e.getMessage());
            throw new BackendException(e);
        }
    }

    @Override
    public void buildQuery() {
        if (queryType.equals(GET_ORDER_ITEMS)) {
            buildGetOrderItemsQuery();

        } else if (queryType.equals(GET_ORDER_IDS)) {
            buildGetOrderIdsQuery();

        } else if (queryType.equals(GET_ORDER_DATA)) {
            buildGetOrderDataQuery();

        } else if (queryType.equals(SUBMIT_ORDER)) {
            buildSaveOrderQuery();

        } else if (queryType.equals(SUBMIT_ORDER_ITEM)) {
            buildSaveOrderItemQuery();

        } else if (queryType.equals(GET_ALL_ORDERS)) {
            buildGetAllOrdersQuery();

        } else if (queryType.equals(DELETE_ORDER)) {
            buildDeleteOrderQuery();

        } else if (queryType.equals(DELETE_ORDER_ITEM)) {
            buildDeleteOrderItemsQuery();
        }
    }

    @Override
    public void populateEntity(ResultSet resultSet)
            throws DatabaseException {
        if (queryType.equals(GET_ORDER_ITEMS)) {
            populateOrderItems(resultSet);

        } else if (queryType.equals(GET_ORDER_IDS)) {
            populateOrderIds(resultSet);

        } else if (queryType.equals(GET_ORDER_DATA)) {
            populateOrderData(resultSet);

        } else if (queryType.equals(GET_ALL_ORDERS)) {
            populateOrders(resultSet);
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

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

    private void deleteOrderItem(int orderId)
            throws DatabaseException {
        queryType = DELETE_ORDER_ITEM;
        this.orderId = orderId;
        dataAccessSS.deleteWithinTransaction(this);
    }

    private void buildSaveOrderQuery() {
        Address shipAddr = orderData.getShipAddress();
        Address billAddr = orderData.getBillAddress();
        CreditCard cc = orderData.getPaymentInfo();
        query = "INSERT into Ord "
                + "(orderid, custid, shipaddress1, shipcity, shipstate, shipzipcode, billaddress1, billcity, billstate,"
                + "billzipcode, nameoncard,  cardnum,cardtype, expdate, orderdate, totalpriceamount, totalshipmentcost,"
                + "totaltaxamount, totalamountcharged  )"
                + "VALUES(NULL," + custProfile.getCustId() + ",'"
                + shipAddr.getStreet1() + "','"
                + shipAddr.getCity() + "','"
                + shipAddr.getState() + "','"
                + shipAddr.getZip() + "','"
                + billAddr.getStreet1() + "','"
                + billAddr.getCity() + "','"
                + billAddr.getState() + "','"
                + billAddr.getZip() + "','"
                + cc.getNameOnCard() + "','"
                + cc.getCardNum() + "','"
                + cc.getCardType() + "','"
                + cc.getExpirationDate() + "','"
                + orderData.getOrderDate() + "',"
                + Double.parseDouble(orderData.getTotalPrice()) + ","
                + Double.parseDouble(orderData.getTotalShipmentCost()) + ","
                + Double.parseDouble(orderData.getTotalTaxAmount()) + ","
                + Double.parseDouble(orderData.getTotalAmountCharged()) + ")";
    }

    private void buildSaveOrderItemQuery() {
        query = "INSERT into OrderItem "
                + "(orderitemid,productid, quantity,totalprice,orderid, shipmentcost,taxamount)"
                + "VALUES(NULL,"
                + orderItem.getProductid() + ","
                + Integer.parseInt(orderItem.getQuantity()) + ","
                + Double.parseDouble(orderItem.getTotalPrice()) + ","
                + orderItem.getOrderid() + ","
                + orderItem.getShipmentCost() + ","
                + orderItem.getTaxAmount() + ")";
    }

    private void buildGetOrderDataQuery() {
        query = "SELECT orderdate, totalpriceamount FROM Ord WHERE orderid = " + orderId;
    }

    private void buildGetOrderIdsQuery() {
        query = "SELECT orderid FROM Ord WHERE custid = " + custProfile.getCustId();
    }

    private void buildGetAllOrdersQuery() {
        query = "SELECT * FROM Ord WHERE custid=" + custProfile.getCustId();
    }

    private void buildGetOrderItemsQuery() {
        query = "SELECT * FROM OrderItem WHERE orderid = " + orderId;
    }

    private void populateOrderItems(ResultSet rs)
            throws DatabaseException {
        orderItems = new LinkedList<OrderItem>();
        OrderItem item;
        try {
            while (rs.next()) {
                item = new OrderItemImpl(rs.getInt("orderitemid"),
                        rs.getInt("productid"),
                        rs.getInt("orderid"),
                        rs.getString("quantity"),
                        rs.getString("totalprice"),
                        rs.getString("shipmentcost"),
                        rs.getString("taxamount"));
                orderItems.add(item);
            }

        } catch (SQLException e) {
            LOG.log(Level.WARNING, "A DatabaseException was thrown: {0}", e.getMessage());
            throw new DatabaseException(e);
        }
    }

    private void populateOrderIds(ResultSet resultSet)
            throws DatabaseException {
        orderIds = new LinkedList<Integer>();
        try {
            while (resultSet.next()) {
                orderIds.add(resultSet.getInt("orderid"));
            }

        } catch (SQLException e) {
            LOG.log(Level.WARNING, "A DatabaseException was thrown: {0}", e.getMessage());
            throw new DatabaseException(e);
        }
    }

    private void populateOrders(ResultSet resultSet)
            throws DatabaseException {
        try {
            allOrders = new ArrayList<Order>();
            while (resultSet.next()) {
                order = new OrderImpl(resultSet.getInt("orderId"), resultSet.getString("orderDate"), resultSet.getString("totalamountcharged"));
                allOrders.add(order);
            }

        } catch (SQLException e) {
            LOG.log(Level.WARNING, "A DatabaseException was thrown: {0}", e.getMessage());
            throw new DatabaseException(e);
        }
    }

    private void populateOrderData(ResultSet resultSet)
            throws DatabaseException {
        try {
            if (resultSet.next()) {
                orderData = new OrderImpl(orderId, resultSet.getString("orderdate"),
                        makeString(resultSet.getDouble("totalpriceamount")));
            }

        } catch (SQLException e) {
            LOG.log(Level.WARNING, "A DatabaseException was thrown: {0}", e.getMessage());
            throw new DatabaseException(e);
        }
    }

    private void buildDeleteOrderQuery() {
        query = "DELETE FROM Ord WHERE orderId= " + orderId;
    }

    private void buildDeleteOrderItemsQuery() {
        query = "DELETE FROM OrderItem WHERE orderId= " + orderId;
    }

    List<Integer> getAllOrderIds(CustomerProfile custProfile)
            throws DatabaseException {
        return new ArrayList<Integer>();
    }

    OrderImpl getOrderData(Integer orderId)
            throws DatabaseException {
        return null;
    }

    // Precondition: CustomerProfile has been set by the constructor
    void submitOrder(ShoppingCart shopCart)
            throws BackendException {
        //strategy:
        //1. get shopcart items and convert each to an order item using OrderUtil method
        //2. create a new order, and add the list of order items
        //3. set address and payment info into the order
        //4. start a transaction
        //5. save order data
        //6. in a loop, save cartitem data one by one
        //7. commit
        //Populate the

        OrderUtil ordUtil = new OrderUtil();

        orderData = new OrderImpl(null, OrderUtil.todaysDateStr(), Double.toString(shopCart.getTotalPrice()));

        orderData.setShipAddress(shopCart.getShippingAddress());
        orderData.setBillAddress(shopCart.getBillingAddress());
        orderData.setCreditCard(shopCart.getPaymentInfo());
        orderData.setTotalShipmentCost(shopCart.getTotalShipmentCost());
        orderData.setTotalTaxAmount(shopCart.getTotalTaxAmount());

        try {

            dataAccessSS.createConnection(this);
            dataAccessSS.startTransaction();

            try {

                queryType = SUBMIT_ORDER;
                orderId = dataAccessSS.save();

                queryType = SUBMIT_ORDER_ITEM;

                for (CartItem cartItem : shopCart.getCartItems()) {
                    orderItem = OrderUtil.createOrderItemFromCartItem(cartItem, orderId);
                    dataAccessSS.save();
                }
                dataAccessSS.commit();
                LOG.info("Order submitted");

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
    private final DataAccessSubsystem dataAccessSS = new DataAccessSubsystemFacade();
    private String query;
    private String queryType;
    private final String GET_ORDER_ITEMS = "GetOrderItems";
    private final String GET_ORDER_IDS = "GetOrderIds";
    private final String GET_ORDER_DATA = "GetOrderData";
    private final String SUBMIT_ORDER = "SubmitOrder";
    private final String SUBMIT_ORDER_ITEM = "SubmitOrderItem";
    private final String GET_ALL_ORDERS = "GetAllOrders";
    private final String DELETE_ORDER = "DeleteOrder";
    private final String DELETE_ORDER_ITEM = "DeleteOrderItem";
    private CustomerProfile custProfile;
    private Integer orderId;
    private Integer customerId;
    private List<Order> allOrders;
    private List<Integer> orderIds;
    private List<OrderItem> orderItems;
    private OrderImpl orderData;
    private OrderItem orderItem;
    private Order order;
    private static final Logger LOG = Logger.getLogger(DbClassOrder.class.getName());

}
