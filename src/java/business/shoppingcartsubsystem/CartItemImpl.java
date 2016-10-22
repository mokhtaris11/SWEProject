package business.shoppingcartsubsystem;

import business.exceptions.BackendException;
import java.util.logging.Logger;

import middleware.exceptions.DatabaseException;
import business.externalinterfaces.CartItem;
import business.externalinterfaces.Product;
import business.externalinterfaces.ProductSubsystem;
import business.productsubsystem.ProductSubsystemFacade;
import java.util.logging.Level;

public class CartItemImpl
        implements CartItem {

    private static final Logger LOG = Logger.getLogger(CartItemImpl.class.getName());

    private Integer cartItemId;
    private Integer cartId;
    private Integer productId;
    private String productName;
    private String quantity;
    private String unitPrice;
    private String totalPrice;
    private String shipmentCost;
    private String taxAmount;
    //this is true if this cart item is data that has come from
    //database
    boolean alreadySaved;

    /**
     * This version of constructor used when reading data from screen
     */
    public CartItemImpl(String productName, String quantity, String totalprice)
            throws BackendException {
        this.productName = productName;
        this.quantity = quantity;
        this.totalPrice = totalprice;
        this.shipmentCost = "0";
        this.taxAmount = "0";
        alreadySaved = false;
        ProductSubsystem prodSS = new ProductSubsystemFacade();
        productId = prodSS.getProductIdFromName(productName);
    }

    /**
     * This version of constructor used when reading from database
     */
    public CartItemImpl(
            Integer cartItemId, Integer cartId,
            Integer productId,
            String quantity,
            String totalPrice,
            boolean alreadySaved)
            throws BackendException {
        this.cartItemId = cartItemId;
        this.cartId = cartId;
        this.productId = productId;
        this.quantity = quantity;
        this.totalPrice = totalPrice;
        this.shipmentCost = "0";
        this.taxAmount = "0";
        this.alreadySaved = alreadySaved;
        ProductSubsystem prodSS = new ProductSubsystemFacade();
        try {
            productName = prodSS.getProductFromId(productId).getProductName();
        } catch (BackendException e) {
            LOG.info(e.getMessage());
        }
    }

    @Override
    public Integer getCartItemId() {
        return cartItemId;
    }

    public void setCartItemId(Integer cartItemId) {
        this.cartItemId = cartItemId;
    }

    @Override
    public Integer getCartId() {
        return cartId;
    }

    @Override
    public void setCartId(Integer cartId) {
        this.cartId = cartId;
    }

    @Override
    public Integer getProductId() {
        return productId;
    }

    @Override
    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    @Override
    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    @Override
    public String getQuantity() {
        return quantity;
    }

    @Override
    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(String unitPrice) {
        this.unitPrice = unitPrice;
    }

    @Override
    public String getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(String totalPrice) {
        this.totalPrice = totalPrice;
    }

    @Override
    public String getShipmentCost() {
        return shipmentCost;
    }

    public void setShipmentCost(String shipmentCost) {
        this.shipmentCost = shipmentCost;
    }

    @Override
    public String getTaxAmount() {
        return taxAmount;
    }

    public void setTaxAmount(String taxAmount) {
        this.taxAmount = taxAmount;
    }

    @Override
    public boolean isAlreadySaved() {
        return alreadySaved;
    }

    public void setAlreadySaved(boolean alreadySaved) {
        this.alreadySaved = alreadySaved;
    }

    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder();
        buf.append("cartItemId = <").append(cartItemId).append(">,");
        buf.append("cartId = <").append(cartId).append(">,");
        buf.append("productId = <").append(productId).append(">,");
        buf.append("quantity = <").append(quantity).append(">,");
        buf.append("totalPrice = <").append(totalPrice).append(">,");
        buf.append("shipmentCost = <").append(shipmentCost).append(">,");
        buf.append("taxAmount = <").append(taxAmount).append(">,");
        buf.append("alreadySaved = <").append(alreadySaved).append(">");
        return buf.toString();
    }

    //new added jan 16
    public String getItemUnitPrice() {
        ProductSubsystem prodSS = new ProductSubsystemFacade();
        try {
            Product prod = prodSS.getProduct(productName);
            unitPrice = prod.getUnitPrice();
        } catch (BackendException ex) {
            LOG.severe(ex.getMessage());
        }
        return unitPrice;
    }

    //New recalculate total price when quantity is changed 
    public String computeTotalPrice() {
        totalPrice = Double.toString(new Double(unitPrice) * new Integer(quantity));
        return totalPrice;
    }

}
