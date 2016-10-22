package business.externalinterfaces;

import java.util.List;

import business.exceptions.BackendException;

import middleware.exceptions.DatabaseException;

public interface OrderSubsystem {

    /**
     * used by customer subsystem at login to obtain this customer's order history from the database. Assumes
     * cust id has already been stored into the order subsystem facade
     */
    List<Order> getOrderHistory()
            throws BackendException;

    /**
     * used by customer subsystem when a final order is submitted; this method extracts order and order items
     * from the passed in shopping cart and saves to database using data access subsystem
     */
    void submitOrder(ShoppingCart shopCart)
            throws BackendException;

    /**
     * used whenever an order item needs to be created from outside the order subsystem
     */
    OrderItem createOrderItem(Integer prodId, Integer orderId, String quantityReq, String totalPrice, String shipmentCost, String taxAmount);

    /**
     * to create an Order object from outside the subsystem
     */
    Order createOrder(Integer orderId, String orderDate, String totalPrice);

    /**
     * gets order items from orderid
     */
    public List<OrderItem> getOrderItems(Integer orderId)
            throws BackendException;

    /**
     * For test purpose
     */
    public DbClassOrderForTest getDbClassOrderForTest();

}
