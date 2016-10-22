package business.util;

import business.exceptions.BackendException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import business.externalinterfaces.CartItem;
import business.externalinterfaces.Order;
import business.externalinterfaces.OrderItem;
import business.externalinterfaces.OrderSubsystem;
import business.externalinterfaces.Product;
import business.ordersubsystem.OrderSubsystemFacade;
import business.productsubsystem.ProductSubsystemFacade;

public class OrderUtil {

    public static String STANDARD_DATE_FORMAT = "MM/dd/yyyy";

    public static String todaysDateStr() {
        Date d = new Date();
        DateFormat f = new SimpleDateFormat(STANDARD_DATE_FORMAT);
        return f.format(d);
    }

    /**
     * convenience method that uses createOrderItemFromCartItem and computeTotalPrice(List orderItems), used
     * for quickly assessing total price in preparing data for Credit Verif system.
     *
     */
    public static String quickComputeTotalPrice(List<CartItem> items) {
        List<OrderItem> list = new ArrayList<OrderItem>();
        for (CartItem item : items) {
            list.add(createOrderItemFromCartItem(item, null));
        }
        return computeTotalPrice(list);
    }

    public static OrderItem createOrderItemFromCartItem(CartItem item, Integer orderId) {
        OrderSubsystem orderSS = new OrderSubsystemFacade(null);
        OrderItem orderItem
                = orderSS.createOrderItem(item.getProductId(),
                        orderId,
                        item.getQuantity(),
                        item.getTotalPrice(),
                        item.getShipmentCost(),
                        item.getTaxAmount());
        return orderItem;
    }

    public static String computeTotalPrice(List<OrderItem> orderItems) {
        String totalprice = "0";
        if (orderItems != null) {
            for (OrderItem item : orderItems) {
                totalprice = StringParse.addDoubles(totalprice, item.getTotalPrice());
            }
        }
        return totalprice;
    }

    public static List<String[]> makeItemsDisplayable(List<OrderItem> orderItems)
            throws BackendException {
        if (orderItems == null) {
            return new LinkedList<String[]>();
        }
        final int NAME = 0;
        final int QUANTITY = 1;
        final int UNIT_PRICE = 2;
        final int TOTAL_PRICE = 3;
        TwoKeyHashMap<Integer, String, Product> productTable = (new ProductSubsystemFacade()).getProductTable();
        List<String[]> returnVal = new LinkedList<String[]>();
        String[] displayableData = null;
        String totalPrice = null;
        String nextQuantity = null;
        String prodName = null;
        Integer nextProdId = null;
        Product nextProduct = null;
        for (OrderItem nextItem : orderItems) {
            displayableData = new String[4];
            nextProdId = nextItem.getProductid();
            nextProduct = productTable.getValWithFirstKey(nextProdId);
            displayableData[NAME] = nextProduct.getProductName();
            nextQuantity = nextItem.getQuantity();
            displayableData[QUANTITY] = nextQuantity;
            totalPrice = nextItem.getTotalPrice();
            displayableData[TOTAL_PRICE] = totalPrice;
            displayableData[UNIT_PRICE]
                    = StringParse.divideDoubles(totalPrice, nextQuantity);
            returnVal.add(displayableData);
        }
        return returnVal;

    }

    /**
     * Makes order data from lower layers available to the GUI, so all values are converted to Strings
     */
    public static List<String[]> extractOrderData(List<Order> ordersList) {
        final int ORDER_ID = 0;
        final int ORDER_DATE = 1;
        final int TOTAL = 2;
        List<String[]> returnVal = new LinkedList<String[]>();

        String[] displayData = null;
        if (ordersList != null) {
            for (Order nextOrder : ordersList) {
                displayData = new String[3];
                displayData[ORDER_ID] = nextOrder.getOrderId().toString();
                displayData[ORDER_DATE] = nextOrder.getOrderDate();
                displayData[TOTAL] = nextOrder.getTotalPrice();
                returnVal.add(displayData);

            }
        }
        return returnVal;

    }

}
