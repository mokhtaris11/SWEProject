package business.rulesbeans;

import business.exceptions.BackendException;
import business.externalinterfaces.Address;
import business.externalinterfaces.CartItem;
import business.externalinterfaces.CreditCard;
import business.externalinterfaces.DynamicBean;
import business.externalinterfaces.ShoppingCart;
import business.util.Pair;
import business.util.ShoppingCartUtil;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.List;

public class FinalOrderBean
        implements DynamicBean {

    public FinalOrderBean(ShoppingCart sc) {
        shopCart = sc;
    }

    ///////bean interface for shopping cart
    public Address getShippingAddress() {
        return shopCart.getShippingAddress();
    }

    public Address getBillingAddress() {
        return shopCart.getBillingAddress();
    }

    public CreditCard getPaymentInfo() {
        return shopCart.getPaymentInfo();
    }

    public List<CartItem> getCartItems() {
        return shopCart.getCartItems();
    }

    /**
     * This is a collection of pairs indicating the quantity requested vs quantity avail for each line item in
     * the shopping cart
     *
     * If return value is empty, this indicates an error condition
     *
     * @return
     */
    public List<Pair> getRequestedAvailableList() {
        List<Pair> retVal = new ArrayList<Pair>();

        try {
            retVal = ShoppingCartUtil.computeRequestedAvailableList(shopCart);
        } catch (BackendException ex) {
            //log.warning("Unable to read database for handling requested/avail rule");
        }
        return retVal;
    }

    @Override
    public void addPropertyChangeListener(PropertyChangeListener pcl) {
        pcs.addPropertyChangeListener(pcl);
    }

    @Override
    public void removePropertyChangeListener(PropertyChangeListener pcl) {
        pcs.removePropertyChangeListener(pcl);
    }
    //private static Logger log = Logger.getLogger(null);
    private final ShoppingCart shopCart;
    ///////////property change listener code
    private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);

}
