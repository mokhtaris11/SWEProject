package business.shoppingcartsubsystem;

import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

import business.exceptions.BackendException;
import business.exceptions.BusinessException;
import business.exceptions.RuleException;
import business.externalinterfaces.Address;
import business.externalinterfaces.CartItem;
import business.externalinterfaces.CreditCard;
import business.externalinterfaces.CustomerProfile;
import business.externalinterfaces.DbClassShoppingCartForTest;
import business.externalinterfaces.Rules;
import business.externalinterfaces.ShoppingCart;
import business.externalinterfaces.ShoppingCartSubsystem;
import java.util.ArrayList;

public class ShoppingCartSubsystemFacade
        implements ShoppingCartSubsystem {

    ShoppingCart liveCart;
    ShoppingCart savedCart;
    Integer shopCartId;
    CustomerProfile customerProfile;
    Logger log = Logger.getLogger(this.getClass().getPackage().getName());

    //interface methods
    @Override
    public void setCustomerProfile(CustomerProfile customerProfile) {
        this.customerProfile = customerProfile;
    }

    //used in CustomerSubsystemFacade.initializeCustomer
    @Override
    public void retrieveSavedCart()
            throws BackendException {
        DbClassShoppingCart dbClass = new DbClassShoppingCart();
        savedCart = dbClass.retrieveSavedCart(customerProfile);
    }

    @Override
    public void deleteSavedCart(String cartId)
            throws BackendException {
        if (cartId != null) {

            DbClassShoppingCart dbClass = new DbClassShoppingCart();
            dbClass.deleteAllCartItems(cartId);
            dbClass.deleteCart(cartId);
        }
        clearLiveCart();
    }

    public ShoppingCartSubsystemFacade() {
    }

    @Override
    public void addCartItem(String itemName, String quantity, String totalPrice, Integer pos)
            throws BackendException {
        //if a saved cart has been retrieved, it will be the live cart, unless
        //user has already added items to a new cart
        if (liveCart == null) {
            liveCart = new ShoppingCartImpl(new LinkedList<CartItem>());
        }
        CartItemImpl item = new CartItemImpl(itemName, quantity, totalPrice);
        if (pos == null) {
            liveCart.addItem(item);
        } else {
            liveCart.insertItem(pos, item);
        }
    }

    //for stubbing
    public CartItem createCartItem(String productName,
            String quantity,
            String totalprice)
            throws BackendException {
        return new CartItemImpl(productName, quantity, totalprice);
    }

    @Override
    public boolean deleteCartItem(int pos) {
        return liveCart.deleteCartItem(pos);
    }

    @Override
    public void clearLiveCart() {
        liveCart = null;//.clearCart();
    }

    @Override
    public boolean deleteCartItem(String itemName) {
        return liveCart.deleteCartItem(itemName);
    }

    @Override
    public List<CartItem> getLiveCartItems() {
        if (liveCart == null || liveCart.getCartItems() == null) {
            return new LinkedList<CartItem>();
        } else {
            return liveCart.getCartItems();
        }

    }

    @Override
    public void setShippingAddress(Address addr) {
        //liveCart should be non-null
        if (liveCart != null) {
            liveCart.setShipAddress(addr);
        }
    }

    @Override
    public void setBillingAddress(Address addr) {
        if (liveCart != null) {
            liveCart.setBillAddress(addr);
        }
    }

    @Override
    public void setPaymentInfo(CreditCard cc) {
        if (liveCart != null) {
            liveCart.setPaymentInfo(cc);
        }

    }

    @Override
    public ShoppingCart getLiveCart() {
        return liveCart;
    }

    @Override
    public ShoppingCart getSavedCart() {
        return savedCart;
    }

    @Override
    public void setLiveCart(ShoppingCart cart) {
        List<CartItem> cartItems = (cart == null) ? new ArrayList<CartItem>()
                : cart.getCartItems();
        liveCart = new ShoppingCartImpl(cartItems);
    }

    @Override
    public void makeSavedCartLive() {
        liveCart = savedCart;
    }

    @Override
    public void saveLiveCart()
            throws BackendException {
        DbClassShoppingCart dbClass = new DbClassShoppingCart();
        dbClass.saveCart(customerProfile, liveCart);
        savedCart = liveCart;
    }

    @Override
    public void runShoppingCartRules()
            throws RuleException, BusinessException {
        Rules transferObject = new RulesShoppingCart(liveCart);
        transferObject.runRules();
    }

    @Override
    public void runFinalOrderRules()
            throws RuleException, BusinessException {
        Rules transferObject = new RulesFinalOrder(liveCart);
        transferObject.runRules();
    }

    //FOR TESTING
    public DbClassShoppingCartForTest getGenericDbClassShoppingCart() {
        return new DbClassShoppingCart();
    }
}
