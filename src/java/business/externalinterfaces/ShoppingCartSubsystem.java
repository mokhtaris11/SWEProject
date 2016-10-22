package business.externalinterfaces;

import business.exceptions.BackendException;
import java.util.List;

import business.exceptions.BusinessException;
import business.exceptions.RuleException;
import java.io.Serializable;

public interface ShoppingCartSubsystem
        extends Serializable {

    /**
     * used during customer login -- when login succeeds, customer profile is set in shopping cart subsystem
     */
    public void setCustomerProfile(CustomerProfile custProfile);

    /**
     * used during customer login to cache this customer's saved cart -- performs a database access through
     * data access subsystem
     */
    public void retrieveSavedCart()
            throws BackendException;

    public void deleteSavedCart(String cartId)
            throws BackendException;

    /**
     * removes item from the cart
     */
    public boolean deleteCartItem(String itemName);

    /**
     * removes item from cart by index
     */
    public boolean deleteCartItem(int pos);

    /**
     * needed after order has been submitted
     */
    public void clearLiveCart();

    /**
     * used when a user adds an item to shopping cart -- it creates a new cart item and adds to the list of
     * cart items stored in the shopping cart subsystem's live cart Integer position should be null if there
     * is no concern about position in the list of items -- this parameter is useful when editing the shopping
     * cart.
     */
    public void addCartItem(String itemName, String quantity, String totalPrice, Integer position)
            throws BackendException;

    /**
     * used to display items currently in live shopping cart (for instance, when an item is added to cart and
     * the CartItemsWindow is about to be displayed). the method returns the list of cart items currently
     * stored in the live cart, sitting in the shopping cart subsystem facade
     */
    public List<CartItem> getLiveCartItems();

    /**
     * When a shopping cart is started before a customer logs in, it creates a shopping cart subsystem that is
     * external to any customer. This must be stored in the sessionContext. When customer logs in, the
     * liveCart from this external shop cart subsystem must be inserted into the Customer shop cart subsystem
     * and the shop cart entry in the session context should be removed.
     */
    public void setLiveCart(ShoppingCart cart);

    /**
     * accessor used by customer subsystem to store user's selected ship address during checkout; stores value
     * in shop cart facade
     */
    public void setShippingAddress(Address addr);

    /**
     * accessor used by customer subsystem to store user's selected ship address during checkout; stores value
     * in shop cart facade
     */
    public void setBillingAddress(Address addr);

    /**
     * accessor used by customer subsystem to store user's selected ship address during checkout; stores value
     * in shop cart facade
     */
    public void setPaymentInfo(CreditCard cc);

    /**
     * Used during order submission when order subsystem prepares an order from the shopping cart.
     *
     * @return
     */
    public ShoppingCart getLiveCart();

    /**
     * This is read at login time to determine size of saved cart.
     */
    public ShoppingCart getSavedCart();

    /**
     * used when user choose the option to 'retrieve saved cart' -- which requires that the customer's saved
     * cart be stored in the live cart in the shopping cart subsystem facade
     */
    public void makeSavedCartLive();

    public void saveLiveCart()
            throws BackendException;

    /**
     * used when a user enters Checkout use case by clicking "Proceed to Checkout" -- at that time rules
     * concerning validity of shopping cart are run (for instance, user may not have an empty cart)
     */
    public void runShoppingCartRules()
            throws RuleException, BusinessException;

    /**
     * invoked when user attempts to submit final order -- rules are run to check validity of the order (for
     * example, quantity available for each product order will be checked against quantity requested)
     */
    public void runFinalOrderRules()
            throws RuleException, BusinessException;

}
