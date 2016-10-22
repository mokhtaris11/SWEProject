package business.productsubsystem;

import business.externalinterfaces.Product;
import business.externalinterfaces.ProductFromGui;

//should not be public
public class ProductImpl
        implements Product, ProductFromGui {

    private Integer productId;
    private String productName;
    private String quantityAvail;
    private String unitPrice;
    private String mfgDate;
    private Integer catalogId;
    private String description;

    //this is the usual constructor, obtained when reading a product from the db
    public ProductImpl(Integer pi, String pn, String qa, String up, String md, Integer ci, String d) {
        productId = pi;
        productName = pn;
        quantityAvail = qa;
        unitPrice = up;
        mfgDate = md;
        catalogId = ci;
        description = d;
    }

    //this constructor is used when getting user-entered data in adding a new product
    //must not be made public

    public ProductImpl(String name, String date, String numAvail, String price) {
        this(null, name, numAvail, price, date, null, null);
    }

    /**
     * @return Returns the catalogId.
     */
    @Override
    public Integer getCatalogId() {
        return catalogId;
    }

    /**
     * @return Returns the mfgDate.
     */
    @Override
    public String getMfgDate() {
        return mfgDate;
    }

    /**
     * @return Returns the productId.
     */
    @Override
    public Integer getProductId() {
        return productId;
    }

    /**
     * @return Returns the productName.
     */
    @Override
    public String getProductName() {
        return productName;
    }

    /**
     * @return Returns the quantityAvail.
     */
    @Override
    public String getQuantityAvail() {
        return quantityAvail;
    }

    /**
     * @return Returns the unitPrice.
     */
    @Override
    public String getUnitPrice() {
        return unitPrice;
    }

    /**
     * @return Returns the description.
     */
    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public void setUnitPrice(String p) {
        unitPrice = p;
    }

    @Override
    public void setProductName(String name) {
        productName = name;
    }

    @Override
    public void setQuantityAvail(String val) {
        quantityAvail = val;
    }

    @Override
    public void setMfgDate(String date) {
        mfgDate = date;
    }
}
