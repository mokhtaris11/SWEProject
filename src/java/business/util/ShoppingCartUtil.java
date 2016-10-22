package business.util;

import business.exceptions.BackendException;
import business.externalinterfaces.CartItem;
import business.externalinterfaces.Product;
import business.externalinterfaces.ShoppingCart;
import business.productsubsystem.ProductSubsystemFacade;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

public class ShoppingCartUtil {

    public static List<String[]> makeDisplayableList(List<CartItem> cartItems) {
        if (cartItems == null) {
            return null;
        }
        List<String[]> returnValue = new LinkedList<String[]>();
        for (CartItem item : cartItems) {
            if (!item.getQuantity().equals("0")) {
                returnValue.add(createDisplay(item));
            }
        }
        return returnValue;

    }

    public static String[] createDisplay(CartItem item) {
        if (item == null) {
            return null;
        }
        final int NAME = 0;
        final int QUANTITY = 1;
        final int UNIT_PRICE = 2;
        final int TOTAL_PRICE = 3;
        String[] returnValue = new String[4];
        returnValue[NAME] = item.getProductName();
        String q = item.getQuantity();
        returnValue[QUANTITY] = q;
        String total = item.getTotalPrice();
        returnValue[TOTAL_PRICE] = total;
        returnValue[UNIT_PRICE] = StringParse.divideDoubles(total, q);
        return returnValue;
    }

    public static List<Pair> computeRequestedAvailableList(ShoppingCart shoppingCart)
            throws BackendException {
        if (shoppingCart == null) {
            return null;
        }
        List<CartItem> cartItems = shoppingCart.getCartItems();
        //HashMap<Integer,Integer> reqAvailMap = new HashMap<Integer,Integer>();
        List<Pair> reqAvailList = new ArrayList<Pair>();
        TwoKeyHashMap prodTable = (new ProductSubsystemFacade()).refreshProductTable();
        CartItem nextItem = null;
        String nextName = null;
        String nextQuantRequested = null;
        String nextQuantAvail = null;
        Product nextProduct = null;
        Iterator it = cartItems.iterator();
        while (it.hasNext()) {
            nextItem = (CartItem) it.next();
            nextQuantRequested = nextItem.getQuantity();
            nextName = nextItem.getProductName();
            nextProduct = (Product) prodTable.getValWithSecondKey(nextName);
            nextQuantAvail = nextProduct.getQuantityAvail();
            reqAvailList.add(new Pair(Integer.parseInt(nextQuantRequested),
                    Integer.parseInt(nextQuantAvail)));

        }
        LOG.info(reqAvailList.toString());
        return reqAvailList;
    }

    /**
     * Returns a list of string arrays, where each String array conforms to the sequence of values as they
     * occur in the CartItemsWindow table model.
     */
    public static List<String[]> cartItemsToStringArrays(List<CartItem> items) {
        List<String[]> newData = new ArrayList<String[]>();
        for (CartItem item : items) {
            String unitPrice = StringParse.divideDoubles(item.getTotalPrice(), item.getQuantity());
            newData.add(new String[]{item.getProductName(), item.getQuantity(), unitPrice, item.getTotalPrice()});
            //item.getProductid(),item.getLineitemid(),item.getCartid()});
        }
        return newData;
    }

    private static final Logger LOG = Logger.getLogger(ShoppingCartUtil.class.getName());

}
