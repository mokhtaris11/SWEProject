package business.ordersubsystem;

import java.util.List;

import business.externalinterfaces.Address;
import business.externalinterfaces.CreditCard;
import business.externalinterfaces.Order;
import business.externalinterfaces.OrderItem;

class OrderImpl
        implements Order {

    private Integer orderId;
    private String orderDate;
    private String totalPrice;
    private List<OrderItem> orderItems;
    private Address shipAddress;
    private Address billAddress;
    private CreditCard creditCard;
    private String totalShipmentCost;
    private String totalTaxAmount;

    OrderImpl(Integer orderId, String orderDate, String totalPrice) {

        this.orderId = orderId;
        this.orderDate = orderDate;
        this.totalPrice = totalPrice;
    }

    public void setOrderItems(List<OrderItem> orderItems) {
        this.orderItems = orderItems;
    }

    @Override
    public List<OrderItem> getOrderItems() {
        return orderItems;
    }

    @Override
    public String getOrderDate() {
        return orderDate;
    }

    @Override
    public Integer getOrderId() {
        return orderId;
    }

    @Override
    public String getTotalPrice() {
        return totalPrice;
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
        return Double.toString(Double.parseDouble(totalPrice) + Double.parseDouble(totalShipmentCost) + Double.parseDouble(totalTaxAmount));
    }

    /**
     * @return Returns the billAddress.
     */
    @Override
    public Address getBillAddress() {
        return billAddress;
    }

    /**
     * @param billAddress The billAddress to set.
     */
    public void setBillAddress(Address billAddress) {
        this.billAddress = billAddress;
    }

    /**
     * @param creditCard The creditCard to set.
     */
    public void setCreditCard(CreditCard creditCard) {
        this.creditCard = creditCard;
    }

    /**
     * @return Returns the shipAddress.
     */
    @Override
    public Address getShipAddress() {
        return shipAddress;
    }

    /**
     * @param shipAddress The shipAddress to set.
     */
    public void setShipAddress(Address shipAddress) {
        this.shipAddress = shipAddress;
    }

    @Override
    public CreditCard getPaymentInfo() {

        return creditCard;
    }

}
