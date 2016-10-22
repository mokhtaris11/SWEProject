package business.externalinterfaces;

import business.shoppingcartsubsystem.CartItemImpl;
import java.util.List;

public interface ShoppingCart {

    String getCartId();

    Address getShippingAddress();

    Address getBillingAddress();

    CreditCard getPaymentInfo();

    List<CartItem> getCartItems();

    void setCartItems(List<CartItem> cartItems);

    void clearCart();

    double getTotalPrice();

    String getTotalShipmentCost();

    String getTotalTaxAmount();

    String getTotalAmountCharged();

    boolean deleteCartItem(String name);

    boolean isEmpty();

    void setCartId(String cartId);

    void setShipAddress(Address address);

    void setBillAddress(Address address);

    void setPaymentInfo(CreditCard creditCard);

    void addItem(CartItemImpl item);

    void insertItem(int pos, CartItemImpl item);

    boolean deleteCartItem(int pos);

}
