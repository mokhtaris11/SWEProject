package business.externalinterfaces;

import java.util.List;

import business.Quantity;
import business.exceptions.BackendException;
import business.exceptions.BusinessException;
import business.exceptions.RuleException;
import business.util.TwoKeyHashMap;
import middleware.exceptions.DatabaseException;

public interface ProductSubsystem {

    /**
     * retrieves a twokey hashmap that consists of all products keyed on both name and id
     */
    public TwoKeyHashMap<Integer, String, Product> getProductTable()
            throws BackendException;

    /**
     * same as getProductTable but forces a database read
     */
    public TwoKeyHashMap<Integer, String, Product> refreshProductTable()
            throws BackendException;

    /**
     * retrieves list of catalog names; if this value is already in memory, it is returned from memory
     */
    public List<String> getCatalogNames()
            throws BackendException;

    public Integer getCatalogIdFromType(String catType)
            throws BackendException;

    /**
     * retrieves list getCatalogNames, but forces a database read
     */
    public List<String> refreshCatalogNames()
            throws BackendException;

    /**
     * gets a list of products from the database, based on catalog type; if list is already in memory then
     * database read is avoided
     */
    public List<Product> getProductList(String catType)
            throws BackendException;

    /**
     * like getProductList, but forces a database read
     */
    public List<Product> refreshProductList(String catType)
            throws BackendException;

    /**
     * convenience method to obtain product id for a given product name
     */
    public Integer getProductIdFromName(String prodName)
            throws BackendException;

    /**
     * reads the product object from the database using the product name using a database hit
     */
    public Product getProduct(String prodName)
            throws BackendException;

    /**
     * reads the product from the productid, using a database hit
     */
    public Product getProductFromId(Integer prodId)
            throws BackendException;

    /**
     * saves newly created catalog
     */
    public void saveNewCatalogName(String name)
            throws BackendException;

    /**
     * updates catalog by name
     */
    public void saveEditCatalogName(int catalogId, String name)
            throws BackendException;

    /**
     * delete selected catalog
     */
    public void deleteCatalog(String name)
            throws BackendException;

    /**
     * creates an IProductFromGui when user creates a product
     */
    public ProductFromGui createProduct(String name, String date, String numAvail, String unitPrice);

    /**
     * saves a new product obtained from user input
     */
    public void saveNewProduct(ProductFromGui product, String catalogType)
            throws BackendException;

    /**
     * reads db to determine quantity available; stores in the Quantity argument
     */
    public void readQuantityAvailable(String prodName, Quantity quantity)
            throws BackendException;

    /**
     * Delete Product by Id
     */
    public void deleteProduct(int productId)
            throws BackendException;

    /**
     * Save Edit Product
     */
    public void saveEditProduct(int productId, String catalogType, ProductFromGui productFromGui)
            throws BackendException;
}
