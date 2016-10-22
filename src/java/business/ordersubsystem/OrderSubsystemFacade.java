package business.ordersubsystem;

import business.exceptions.BackendException;
import business.externalinterfaces.CustomerSubsystem;
import business.externalinterfaces.DbClassOrderForTest;
import business.externalinterfaces.Order;
import business.externalinterfaces.OrderItem;
import business.externalinterfaces.OrderSubsystem;
import business.externalinterfaces.ShoppingCart;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;
import middleware.exceptions.DatabaseException;

public class OrderSubsystemFacade
        implements OrderSubsystem {

    public OrderSubsystemFacade() {

    }

    public OrderSubsystemFacade(CustomerSubsystem cust) {
        this.cust = cust;
    }

    @Override
    public List<Order> getOrderHistory()
            throws BackendException {

        DbClassOrder dbClassOrder = new DbClassOrder(cust.getCustomerProfile());
        List<Order> orders;

        orders = dbClassOrder.getAllOrders();
        return Collections.unmodifiableList(orders);

    }

    @Override
    public OrderItem createOrderItem(Integer prodId, Integer orderId, String quantityReq, String totalPrice,
            String shipmentCost, String taxAmount) {
        return new OrderItemImpl(prodId, orderId, quantityReq, totalPrice, shipmentCost, taxAmount);
    }

    @Override
    public Order createOrder(Integer orderId, String orderDate, String totalPrice) {
        return new OrderImpl(orderId, orderDate, totalPrice);
    }

    @Override
    public void submitOrder(ShoppingCart shopCart)
            throws BackendException {
        DbClassOrder dbClassOrder = new DbClassOrder(cust.getCustomerProfile());
        dbClassOrder.submitOrder(shopCart);
        cust.refreshAfterSubmit();
    }

    @Override
    public List<OrderItem> getOrderItems(Integer orderId)
            throws BackendException {

        DbClassOrder dbClass = new DbClassOrder();
        return dbClass.getOrderItems(orderId);

    }

    /**
     * For Test Purpose
     */
    @Override
    public DbClassOrderForTest getDbClassOrderForTest() {
        return new DbClassOrder();
    }

    ///////////// Methods internal to the Order Subsystem
    List<Integer> getAllOrderIds()
            throws DatabaseException {

        DbClassOrder dbClass = new DbClassOrder();
        return dbClass.getAllOrderIds(cust.getCustomerProfile());

    }

    OrderImpl getOrderData(Integer orderId)
            throws DatabaseException {
        DbClassOrder dbClass = new DbClassOrder();
        return dbClass.getOrderData(orderId);
    }

    CustomerSubsystem cust;
    private static final Logger LOG = Logger.getLogger(OrderSubsystemFacade.class.getName());
}
