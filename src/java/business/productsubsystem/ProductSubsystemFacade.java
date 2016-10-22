/*
 * Created on Mar 20, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package business.productsubsystem;

import business.DbClassQuantity;
import business.Quantity;
import business.exceptions.BackendException;
import business.externalinterfaces.CatalogTypes;
import business.externalinterfaces.DbClassCatalogTypesForTest;
import business.externalinterfaces.DbClassProductForTest;
import business.externalinterfaces.Product;
import business.externalinterfaces.ProductFromGui;
import business.externalinterfaces.ProductSubsystem;
import business.util.TwoKeyHashMap;
import java.util.List;
import java.util.logging.Logger;

public class ProductSubsystemFacade
        implements ProductSubsystem {

    @Override
    public TwoKeyHashMap<Integer, String, Product> getProductTable()
            throws BackendException {
        DbClassProduct dbClass = new DbClassProduct();
        return dbClass.readProductTable();

    }

    @Override
    public TwoKeyHashMap<Integer, String, Product> refreshProductTable()
            throws BackendException {
        DbClassProduct dbClass = new DbClassProduct();
        return dbClass.refreshProductTable();
    }

    @Override
    public List<String> refreshCatalogNames()
            throws BackendException {
        DbClassCatalogTypes dbclass = new DbClassCatalogTypes();
        types = dbclass.getCatalogTypes();
        return types.getCatalogNames();
    }

    @Override
    public List<String> getCatalogNames()
            throws BackendException {

        //don't hit database if not needed
        if (types == null) {
            DbClassCatalogTypes dbclass = new DbClassCatalogTypes();
            types = dbclass.getCatalogTypes();
        }
        return types.getCatalogNames();
    }

    @Override
    public List<Product> getProductList(String catType)
            throws BackendException {
        DbClassProduct dbclass = new DbClassProduct();
        Integer catId = getCatalogIdFromType(catType);
        List<Product> list = dbclass.readProductList(catId);

        //this is a read-only list
        LOG.info("returning list");
        return list;
    }

    @Override
    public List<Product> refreshProductList(String catType)
            throws BackendException {
        return getProductList(catType);
    }

    @Override
    public Product getProduct(String prodName)
            throws BackendException {
        return getProductTable().getValWithSecondKey(prodName);
    }

    @Override
    public Product getProductFromId(Integer prodId)
            throws BackendException {
        Product product = null;
        DbClassProduct dbclass = new DbClassProduct();
        return dbclass.readProduct(prodId);
    }

    @Override
    public Integer getProductIdFromName(String prodName)
            throws BackendException {
        return getProductTable().getValWithSecondKey(prodName).getProductId();

    }

    @Override
    public void saveNewCatalogName(String name)
            throws BackendException {
        DbClassCatalog dbCatalog = new DbClassCatalog();
        dbCatalog.saveNewCatalog(name);
    }

    @Override
    public void saveEditCatalogName(int catalogId, String name)
            throws BackendException {
        DbClassCatalog dbCatalog = new DbClassCatalog();
        dbCatalog.saveEditCatalog(catalogId, name);
    }

    @Override
    public void deleteCatalog(String name)
            throws BackendException {
        DbClassCatalog dbCatalog = new DbClassCatalog();
        dbCatalog.deleteCatalog(name);
    }

    @Override
    public Integer getCatalogIdFromType(String catType)
            throws BackendException {
        DbClassCatalogTypes dbclass = new DbClassCatalogTypes();
        types = dbclass.getCatalogTypes();
        return types.getCatalogId(catType);
    }

    @Override
    public ProductFromGui createProduct(String name, String date, String numAvail, String unitPrice) {
        return new ProductImpl(name, date, numAvail, unitPrice);

    }

    /**
     * the input product has only the values given from the gui -- first step is to locate other values
     * needed, and then do the save database columns: productid, productname, totalquantity, priceperunit,
     * mfgdate, catalogid, description
     */
    @Override
    public void saveNewProduct(ProductFromGui product, String catalogType)
            throws BackendException {
        //get catalogid
        Integer catalogid = getCatalogIdFromType(catalogType);
        //invent description
        String description = DEFAULT_PROD_DESCRIPTION;
        DbClassProduct dbclass = new DbClassProduct();
        dbclass.saveNewProduct(product, catalogid, description);

    }
    /* reads quantity avail and stores in the Quantity argument */

    @Override
    public void readQuantityAvailable(String prodName, Quantity quantity)
            throws BackendException {
        DbClassQuantity dbclass = new DbClassQuantity();
        dbclass.setQuantity(quantity);
        dbclass.readQuantityAvail(prodName);
    }

    @Override
    public void deleteProduct(int productId)
            throws BackendException {
        DbClassProduct dbclass = new DbClassProduct();
        dbclass.deleteProduct(productId);
    }

    @Override
    public void saveEditProduct(int productId, String catalogType, ProductFromGui productFromGui)
            throws BackendException {
        DbClassProduct dbclass = new DbClassProduct();
        int catalogid = getCatalogIdFromType(catalogType);
        dbclass.saveEditProduct(productFromGui, catalogid, productId);
    }

    //FOR TESTING
    public DbClassCatalogTypesForTest getGenericDbClassCatalogTypes() {
        return new DbClassCatalogTypes();
    }

    public DbClassProductForTest getGenericDbClassProduct() {
        return new DbClassProduct();
    }

    final String DEFAULT_PROD_DESCRIPTION = "New Product";
    CatalogTypes types;

    private static final Logger LOG = Logger.getLogger(ProductSubsystemFacade.class.getName());

}
