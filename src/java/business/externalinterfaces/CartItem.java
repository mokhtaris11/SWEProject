package business.externalinterfaces;

public interface CartItem {

    public Integer getCartItemId();

    public Integer getCartId();

    public Integer getProductId();

    public String getProductName();

    public String getQuantity();

    public String getTotalPrice();

    public String getShipmentCost();

    public String getTaxAmount();

    public boolean isAlreadySaved();

    public void setCartId(Integer cartId);

    public void setProductId(Integer productId);

    public void setQuantity(String quantity);
}
