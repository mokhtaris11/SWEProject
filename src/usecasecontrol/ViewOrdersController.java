package usecasecontrol;

import business.exceptions.BackendException;
import business.externalinterfaces.CustomerSubsystem;
import business.externalinterfaces.Order;
import business.externalinterfaces.OrderItem;
import business.externalinterfaces.OrderSubsystem;
import business.externalinterfaces.ProductSubsystem;
import business.ordersubsystem.OrderSubsystemFacade;
import business.productsubsystem.ProductSubsystemFacade;
import java.util.ArrayList;
import java.util.List;

public class ViewOrdersController {

    public List<Order> getOrders(CustomerSubsystem cust)
            throws BackendException {
        //OrderSubsystem order = new OrderSubsystemFacade(cust);
        //return order.getOrderHistory();
        if (cust == null) {
            return new ArrayList<Order>();
        }
        return cust.getOrderHistory();
    }

    public List<OrderItem> getOrderItemsForOrderId(int orderId)
            throws BackendException {
        OrderSubsystem oss = new OrderSubsystemFacade(null);
        return oss.getOrderItems(orderId);
    }

    public String getProductName(int productId)
            throws BackendException {
        ProductSubsystem pss = new ProductSubsystemFacade();
        return pss.getProductFromId(productId).getProductName();
    }

    public String getProductUnitPrice(int productId)
            throws BackendException {
        ProductSubsystem pss = new ProductSubsystemFacade();
        return pss.getProductFromId(productId).getUnitPrice();
    }

}
