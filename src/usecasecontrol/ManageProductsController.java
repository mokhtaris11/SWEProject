package usecasecontrol;

import business.exceptions.BackendException;
import business.externalinterfaces.Product;
import business.externalinterfaces.ProductFromGui;
import business.externalinterfaces.ProductSubsystem;
import business.productsubsystem.ProductSubsystemFacade;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class ManageProductsController {

    ProductSubsystem pss;
    private static final Logger LOG
            = Logger.getLogger(ManageProductsController.class.getName());

    public List<Product> getProductsList(String catalog)
            throws BackendException {
        pss = new ProductSubsystemFacade();
        return pss.getProductList(catalog);
    }

    public List<String> getCatalogList()
            throws BackendException {
        pss = new ProductSubsystemFacade();
        return pss.getCatalogNames();

    }

    public int readCatalogIdByCatalogName(String name)
            throws BackendException {
        pss = new ProductSubsystemFacade();
        return pss.getCatalogIdFromType(name);
    }

    public void saveEditCatalogName(int id, String name)
            throws BackendException {
        pss = new ProductSubsystemFacade();
        pss.saveEditCatalogName(id, name);
    }

    public void saveNewCatalogName(String name)
            throws BackendException {
        pss = new ProductSubsystemFacade();
        pss.saveNewCatalogName(name);
    }

    public void saveNewProduct(String catalogSelected, String prodName,
            String mfDate, String quantityAvail, String unitPrice)
            throws BackendException {
        pss = new ProductSubsystemFacade();
        ProductFromGui p = pss.createProduct(prodName, mfDate, quantityAvail, unitPrice);
        pss.saveNewProduct(p, catalogSelected);
    }

    public void deleteProduct(int productId)
            throws BackendException {
        pss = new ProductSubsystemFacade();
        pss.deleteProduct(productId);
    }

    public void deleteCatalog(String catalogName)
            throws BackendException {
        pss = new ProductSubsystemFacade();
        pss.deleteCatalog(catalogName);
    }

    public void saveEditProduct(int productId, String catalogType, String prodName,
            String mfDate, String quantityAvail, String unitPrice)
            throws BackendException {
        pss = new ProductSubsystemFacade();
        ProductFromGui productFromGui = pss.createProduct(prodName, mfDate, quantityAvail, unitPrice);
        pss.saveEditProduct(productId, catalogType, productFromGui);
    }

}
