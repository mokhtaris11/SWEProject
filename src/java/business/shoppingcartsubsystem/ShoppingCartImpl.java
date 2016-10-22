package business.shoppingcartsubsystem;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import business.externalinterfaces.Address;
import business.externalinterfaces.CartItem;
import business.externalinterfaces.CreditCard;
import business.externalinterfaces.ShoppingCart;

class ShoppingCartImpl
        implements ShoppingCart {

    private String cartId;
    private List<CartItem> cartItems;
    private Address shipAddress;
    private Address billAddress;
    private CreditCard creditCard;
    private String totalShipmentCost;
    private String totalTaxAmount;

    ShoppingCartImpl(List<CartItem> cartItems) {
        this.cartItems = cartItems;
        totalShipmentCost = "0";
        totalTaxAmount = "0";
    }

    ShoppingCartImpl() {
        cartItems = new ArrayList<CartItem>();
        totalShipmentCost = "0";
        totalTaxAmount = "0";
    }

    @Override
    public void setCartId(String cartId) {
        this.cartId = cartId;
    }

    @Override
    public String getCartId() {
        return cartId;
    }

    @Override
    public boolean isEmpty() {
        return cartItems == null || cartItems.isEmpty();
    }

    @Override
    public void addItem(CartItemImpl item) {
        if (cartItems == null) {
            cartItems = new LinkedList<CartItem>();
        }
        cartItems.add(item);
    }

    @Override
    public void insertItem(int pos, CartItemImpl item) {
        if (cartItems == null || pos >= cartItems.size()) {
            addItem(item);

        } else {
            cartItems.add(pos, item);
        }
    }

    @Override
    public List<CartItem> getCartItems() {
        return cartItems;
    }

    @Override
    public void setShipAddress(Address addr) {
        shipAddress = addr;
    }

    @Override
    public void setBillAddress(Address addr) {
        billAddress = addr;
    }

    @Override
    public void setPaymentInfo(CreditCard cc) {
        creditCard = cc;
    }

    @Override
    public Address getShippingAddress() {
        return shipAddress;
    }

    @Override
    public Address getBillingAddress() {
        return billAddress;
    }

    @Override
    public CreditCard getPaymentInfo() {
        return creditCard;
    }

    @Override
    public boolean deleteCartItem(int pos) {
        Object ob = cartItems.remove(pos);
        return (ob != null);
    }

    @Override
    public boolean deleteCartItem(String name) {
        CartItem itemSought = null;
        for (CartItem item : cartItems) {
            if (item.getProductName().equals(name)) {
                itemSought = item;
            }
        }
        Object ob = cartItems.remove(itemSought);
        return (ob != null);
    }

    @Override
    public void clearCart() {
        cartItems.clear();
    }

    @Override
    public double getTotalPrice() {
        double sum = 0.00;

        Iterator<CartItem> itr = cartItems.iterator();
        while (itr.hasNext()) {
            CartItem item = itr.next();
            sum += Double.parseDouble(item.getTotalPrice());
        }
        return sum;
    }

    @Override
    public void setCartItems(List<CartItem> cartItems) {
        this.cartItems = cartItems;
    }

    @Override
    public String getTotalShipmentCost() {
        return totalShipmentCost;
    }

    public void setTotalShipmentCost(String totalShipmentCost) {
        this.totalShipmentCost = totalShipmentCost;
    }

    @Override
    public String getTotalTaxAmount() {
        return totalTaxAmount;
    }

    public void setTotalTaxAmount(String totalTaxAmount) {
        this.totalTaxAmount = totalTaxAmount;
    }

    @Override
    public String getTotalAmountCharged() {
        return Double.toString(getTotalPrice() + Double.parseDouble(totalShipmentCost) + Double.parseDouble(
                totalTaxAmount));
    }

}
