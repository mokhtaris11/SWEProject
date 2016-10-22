package presentation.data;

import business.exceptions.BackendException;
import business.exceptions.UnauthorizedException;
import business.externalinterfaces.Product;
import business.externalinterfaces.ProductFromGui;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.enterprise.context.SessionScoped;
import javax.faces.event.ValueChangeEvent;
import javax.inject.Inject;
import javax.inject.Named;
import presentation.control.Authorization;
import presentation.control.Callback;
import usecasecontrol.ManageProductsController;

@Named(value = "manageProductsPCB")
@SessionScoped
public class ManageProductsPCB
        implements Serializable {

    private static final Logger LOG = Logger.getLogger(BrowseSelectPCB.class.getName());
    private String catalogSelected;
    private int catalogIdSelected;
    private List<String> catalogs = new ArrayList<String>();
    private List<Product> products = new ArrayList<Product>();
    private ManageProductsController prodsControl = new ManageProductsController();
    private final String DEFAULT_CATALOG = "Books";
    private HashMap<String, Boolean> checked = new HashMap<String, Boolean>();
    private String selectedProductName;
    private boolean disabled = true;
    private String prodName;
    private String unitPrice;
    private String mfDate;
    private String quantityAvail;
    private Product productToEdit;
    @Inject
    private SessionData sessionContext;

    public Product getProductToEdit() {
        return productToEdit;
    }

    public void setProductToEdit(Product productToEdit) {
        this.productToEdit = productToEdit;
    }

    public List<String> getCatalogs() {
        if (catalogs == null || catalogs.isEmpty()) {
            try {
                catalogs = prodsControl.getCatalogList();
            } catch (BackendException e) {
                MessagesUtil.displayError(e.getMessage());
                LOG.warning("Retreive Catalogs fails");
                return new ArrayList<String>();
            }
        }
        return catalogs;
    }

    //Gets the products related to the selected catalog
    public List<Product> getProducts() {
        try {
            String catalog = getCatalogSelected();
            products = prodsControl.getProductsList(catalog);
            return products;
        } catch (BackendException e) {
            MessagesUtil.displayError(e.getMessage());
            LOG.warning("Retreive Product fails");
            return new ArrayList<Product>();
        }
    }

    public boolean manageProducts(Requirement r) {
        //Authorize first
        try {
            Authorization.checkAuthorization(r, sessionContext.custIsAdmin());
        } catch (UnauthorizedException e) {
            MessagesUtil.displayError(e.getMessage());
            return false;
        }

        catalogs = getCatalogs();
        products = getProducts();
        return true;

    }

    public boolean manageCatalogs(Requirement r) {
        //Authorize first
        try {
            Authorization.checkAuthorization(r, sessionContext.custIsAdmin());
        } catch (UnauthorizedException e) {
            MessagesUtil.displayError(e.getMessage());
            return false;
        }

        catalogs = getCatalogs();
        return true;

    }

    public void changeProductsForNewCatalog(ValueChangeEvent event) {

        products.clear();

        catalogSelected = event.getNewValue().toString();

        products = getProducts();

    }

    public String getCatalogSelected() {
        return (catalogSelected == null) ? DEFAULT_CATALOG : catalogSelected;
    }

    public void setCatalogSelected(String catalogSelected) {
        this.catalogSelected = catalogSelected;
    }

    public HashMap<String, Boolean> getChecked() {
        return checked;
    }

    public void setChecked(HashMap<String, Boolean> checked) {
        this.checked = checked;
    }

    public String getProdName() {
        return prodName;
    }

    public void setProdName(String prodName) {
        this.prodName = prodName;
    }

    public String getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(String unitPrice) {
        this.unitPrice = unitPrice;
    }

    public String getMfDate() {
        return mfDate;
    }

    public void setMfDate(String mfDate) {
        this.mfDate = mfDate;
    }

    public String getQuantityAvail() {
        return quantityAvail;
    }

    public void setQuantityAvail(String quantityAvail) {
        this.quantityAvail = quantityAvail;
    }

    public boolean isDisabled() {
        return disabled;
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

    public String getSelectedProductName() {
        return selectedProductName;
    }

    public void setSelectedProductName(String selectedProductName) {
        this.selectedProductName = selectedProductName;
    }

    private String newCatalogName;

    public String showAddCatalog() {
        setNewCatalogName("");
        checked.clear();
        return "addCatalog";
    }

    public String editCatalogName() {
        try {
            for (String cat : catalogs) {
                if (checked.get(cat)) {
                    catalogSelected = cat;
                    catalogIdSelected = prodsControl.readCatalogIdByCatalogName(cat);
                    return "editCatalog";
                }
            }
        } catch (BackendException ex) {
            MessagesUtil.displayError(ex.getMessage());
            LOG.log(Level.WARNING, "Update catalog name fail: {0}", catalogSelected);
        }
        //return the page itself if no catalog is selected
        return null;
    }

    public String saveEditedCatalogName() {
        try {
            prodsControl.saveEditCatalogName(catalogIdSelected, catalogSelected);
            catalogs = null;
        } catch (BackendException ex) {
            MessagesUtil.displayError(ex.getMessage());
            LOG.log(Level.WARNING, "Update catalog name fail:{0}", catalogSelected);
        }
        return "manageCatalogsWindow";
    }

    public String getNewCatalogName() {
        return newCatalogName;
    }

    public void setNewCatalogName(String newCatalogName) {
        this.newCatalogName = newCatalogName;
    }

    public String addCatalog() {
        try {
            prodsControl.saveNewCatalogName(newCatalogName);
            catalogs = null;
        } catch (BackendException ex) {
            MessagesUtil.displayError(ex.getMessage());
            LOG.log(Level.WARNING, "Save catalog name fail:{0}", newCatalogName);
        }
        return "manageCatalogsWindow";
    }

    public String deleteCatalog() {
        try {
            List<String> catalogsToBeRemoved = new ArrayList<String>();
            for (String cat : catalogs) {
                if (checked.get(cat)) {
                    prodsControl.deleteCatalog(cat);
                    catalogsToBeRemoved.add(cat);
                }
            }
            catalogs = null;
        } catch (BackendException ex) {
            ex.printStackTrace();
            LOG.log(Level.WARNING, "Delete catalog fail:{0}", catalogSelected);
        }
        return "manageCatalogsWindow";
    }

    public String addNewproduct() {
        setMfDate(null);
        setQuantityAvail(null);
        setUnitPrice(null);

        setProdName(null);

        return "addNewproduct";
    }

    public String saveNewProduct() {
        try {
            prodsControl.saveNewProduct(catalogSelected, prodName, mfDate, quantityAvail, unitPrice);
        } catch (BackendException e) {
            MessagesUtil.displayError(e.getMessage());
            LOG.log(Level.WARNING, "Save new product fail:{0}", prodName);
        }

        return "manageProductsWindow";
    }

    public String editProduct() {

        for (Product p : products) {

            if (checked.get(p.getProductName())) {
                productToEdit = p;
                checked.clear();
                return "editProduct";
            }
        }
        //return the page itself if no product is selected
        return null;

    }

    public String saveEditedProduct() {
        String productNameLog = "";
        try {
            for (Product p : products) {

                if (p.getCatalogId().equals(productToEdit.getCatalogId())
                        && p.getMfgDate().equals(productToEdit.getMfgDate()) && p.getProductName().equals(productToEdit.getProductName())) {
                    productNameLog = p.getProductName();
                    prodsControl.saveEditProduct(p.getProductId(), getCatalogSelected(), productToEdit.getProductName(), productToEdit.getMfgDate(), productToEdit.getQuantityAvail(), productToEdit.getUnitPrice());

                }
            }
        } catch (BackendException ex) {
            MessagesUtil.displayError(ex.getMessage());
            LOG.log(Level.WARNING, "Save edit catalog fail:{0}", productNameLog);
        }

        return "manageProductsWindow";
    }

    public String deleteProduct() {
        String productLog = "";
        try {
            for (Product p : products) {
                if (checked.get(p.getProductName())) {
                    productLog = p.getProductName();
                    prodsControl.deleteProduct(p.getProductId());

                }
            }
        } catch (BackendException ex) {
            MessagesUtil.displayError(ex.getMessage());
            LOG.log(Level.WARNING, "Delete catalog fail:{0}", productLog);
        }
        return null;
    }

    public ManageProductsCallback getManageProductsCallback() {
        return new ManageProductsCallback();
    }

    public class ManageProductsCallback
            implements Callback {

        public final Requirement REQUIREMENT = Requirement.MANAGE_PRODUCTS;

        @Override
        public String doUpdate() {
            if (manageProducts(REQUIREMENT)) {
                return null;
            }
            return "index";
        }
    }

    public ManageCatalogsCallback getManageCatalogsCallback() {
        return new ManageCatalogsCallback();
    }

    public class ManageCatalogsCallback
            implements Callback {

        public final Requirement REQUIREMENT = Requirement.MANAGE_CATALOGS;

        @Override
        public String doUpdate() {
            if (manageCatalogs(REQUIREMENT)) {
                return null;
            }
            return "index";
        }
    }
}
