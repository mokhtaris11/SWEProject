package business.ordersubsystem;

import business.externalinterfaces.OrderItem;

public class OrderItemImpl
        implements OrderItem {

    Integer lineitemid;
    Integer productid;
    Integer orderid;
    String quantity;
    String totalPrice;
    String shipmentCost;
    String taxAmount;

    /**
     * Used for reading data from database
     */
    public OrderItemImpl(Integer lineitemid, Integer productid, Integer orderid, String quantity, String totalPrice, String shipmentCost, String taxAmount) {
        this.lineitemid = lineitemid;
        this.productid = productid;
        this.orderid = orderid;
        this.quantity = quantity;
        this.totalPrice = totalPrice;
        this.shipmentCost = shipmentCost;
        this.taxAmount = taxAmount;
    }

    /**
     * Used for creating order item to send to dbase
     */
    public OrderItemImpl(Integer productid, Integer orderid, String quantity, String totalPrice, String shipmentCost, String taxAmount) {

        this.productid = productid;
        this.orderid = orderid;
        this.quantity = quantity;
        this.totalPrice = totalPrice;
        this.shipmentCost = shipmentCost;
        this.taxAmount = taxAmount;
    }

    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder();
        buf.append("lineitemid: <").append(lineitemid).append(">,");
        buf.append("productid: <").append(productid).append(">,");
        buf.append("orderid: <").append(orderid).append(">,");
        buf.append("quantity: <").append(quantity).append(">,");
        buf.append("totalPrice: <").append(totalPrice).append(">");
        buf.append("shipmentCost: <").append(shipmentCost).append(">");
        buf.append("taxAmount: <").append(taxAmount).append(">");
        return buf.toString();
    }

    @Override
    public void setLineItemId(Integer lid) {
        lineitemid = lid;
    }

    /**
     * When submitting an order, orderid is not known initially; after order level is submitted, orderid can
     * be read and inserted into orderitems
     */
    @Override
    public void setOrderid(Integer orderid) {
        this.orderid = orderid;
    }

    @Override
    public Integer getLineitemid() {
        return lineitemid;
    }

    @Override
    public Integer getProductid() {
        return productid;
    }

    @Override
    public Integer getOrderid() {
        return orderid;
    }

    @Override
    public String getQuantity() {
        return quantity;
    }

    @Override
    public String getTotalPrice() {
        return totalPrice;
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

}
