package business.externalinterfaces;

public interface Product {

    public Integer getCatalogId();

    public String getMfgDate();

    public Integer getProductId();

    public String getProductName();

    public String getQuantityAvail();

    public String getUnitPrice();

    public String getDescription();

    public void setUnitPrice(String p);

    public void setProductName(String name);

    public void setQuantityAvail(String val);

    public void setMfgDate(String date);
}
