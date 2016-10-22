/*
 * This is a Presentation Control Bean for the browse and select use case.  It
 * is intended to hold JSF action methods for the Browse and
 * Select use case.
 *
 */
package presentation.data;

import business.exceptions.BackendException;
import business.exceptions.BusinessException;
import business.exceptions.RuleException;
import business.exceptions.UnauthorizedException;
import business.externalinterfaces.CartItem;
import business.externalinterfaces.Product;
import business.externalinterfaces.ShoppingCartSubsystem;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.ConfigurableNavigationHandler;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.inject.Inject;
import javax.inject.Named;
import middleware.exceptions.DatabaseException;
import presentation.control.Authorization;
import presentation.control.Callback;
import usecasecontrol.BrowseSelectController;

@Named("bsPCB")
@SessionScoped
public class BrowseSelectPCB
        implements Serializable {

    BrowseSelectPCB() {
    }

    public boolean isAlreadyAdded() {
        return alreadyAdded;
    }

    public void setAlreadyAdded(boolean alreadyAdded) {
        this.alreadyAdded = alreadyAdded;
    }

    public SessionData getSessionContext() {
        return sessionContext;
    }

    public int getNumberOfItems() {
        numberOfItems = cartItems.size();
        return numberOfItems;
    }

    public HashMap<String, Boolean> getChecked() {
        return checked;
    }

    public void setChecked(HashMap<String, Boolean> checked) {
        this.checked = checked;
    }

    public boolean isEditableItem() {
        return editableItem;
    }

    public void setEditableItem(boolean editableItem) {
        this.editableItem = editableItem;
    }

    public HashMap<String, Boolean> getCheckedForEdit() {
        return checkedForEdit;
    }

    public void setCheckedForEdit(HashMap<String, Boolean> checkedForEdit) {
        this.checkedForEdit = checkedForEdit;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public String getCatalog() {
        return catalog;
    }

    public void setCatalog(String catalog) {
        this.catalog = catalog;
    }

    public String getQuantityRequested() {
        return quantityRequested;
    }

    public void setQuantityRequested(String q) {
        this.quantityRequested = q;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public SelectItem[] getCatalogs() {
        SelectItem[] catalogs = null;
        try {
            List<String> cats = bsController.getCatalogNames();
            catalogs = new SelectItem[cats.size()];
            int count = 0;
            for (String cat : cats) {
                catalogs[count++] = new SelectItem(cat);
            }
            LOG.log(Level.INFO, "catalog count {0}", count);
        } catch (BackendException e) {
            LOG.log(Level.WARNING, "A BackendException was thrown: {0}", e.getMessage());
            MessagesUtil.displayError(e.getMessage());
        }
        return catalogs;
    }

    public SelectItem[] getProducts()
            throws DatabaseException {
        SelectItem[] products = null;
        try {
            List<Product> prods = bsController.getProducts(catalog);
            products = new SelectItem[prods.size()];
            int count = 0;
            for (Product prod : prods) {
                products[count++] = new SelectItem(prod.getProductName());
            }

        } catch (BackendException e) {
            LOG.log(Level.WARNING, "A BackendException was thrown: {0}", e.getMessage());
            MessagesUtil.displayError(e.getMessage());
        }
        return products;
    }

    public String addToCart() {
        return "quantity";
    }

    public String addQuantity() {
        int quant = Integer.parseInt(quantityRequested);
        try {
            ShoppingCartSubsystem ss = obtainCurrentShoppingCartSubsystem();
            //ShoppingCartSubsystem ss = obtainCurrentShoppingCartSubsystem();
            //put the item inside a shopping cart
            bsController.addCartItem(ss, productName, quant);
        } catch (BackendException ex) {
            LOG.log(Level.WARNING, "A BackendException was thrown: {0}", ex.getMessage());
            MessagesUtil.displayError(ex.getMessage());
        }
        return "cartItems";
    }

    public void retrieveSavedCart(Requirement r) {
        //First authorize
        try {
            Authorization.checkAuthorization(r, sessionContext.custIsAdmin());
        } catch (UnauthorizedException e) {
            MessagesUtil.displayError(e.getMessage());
            return;
        }
        cartItems = bsController.retrieveSavedCart(sessionContext.getCust());
    }

    public void saveCart(Requirement r) {
        //Authorize first
        try {
            Authorization.checkAuthorization(r, sessionContext.custIsAdmin());
        } catch (UnauthorizedException e) {
            MessagesUtil.displayError(e.getMessage());
            return;
        }
        try {
            bsController.saveCart(sessionContext.getCust());
        } catch (BackendException e) {
            LOG.log(Level.WARNING, "A BackendException was thrown: {0}", e.getMessage());
            MessagesUtil.displayError(e.getMessage());
        }
    }

    public List<CartItem> getCartItems() {
        cartItems = bsController.getCartItems(obtainCurrentShoppingCartSubsystem());
        return cartItems;
    }

    // @param cartItems the cartItems to set
    public void setCartItems(List<CartItem> Items) {
        this.cartItems = Items;
    }

    public Product getProduct() {
        setAlreadyAdded(false);
        for (CartItem cartItem : bsController.getCartItems(obtainCurrentShoppingCartSubsystem())) {
            if (cartItem.getProductName().equalsIgnoreCase(productName)) {
                setAlreadyAdded(true);
                break;
            }
        }

        try {
            product = bsController.getProduct(productName);
        } catch (BackendException e) {
            LOG.log(Level.WARNING, "A BackendException was thrown: {0}", e.getMessage());
            MessagesUtil.displayError(e.getMessage());
        }
        return product;
    }

    public CartItem[] changeListtoArray() {
        CartItem[] itemArray = new CartItem[cartItems.size()];

        for (int i = 0; i < itemArray.length; i++) {
            CartItem item = cartItems.get(i);
            itemArray[i] = item;
        }
        return itemArray;
    }

    public void deleteSelectedItems() {
        List<CartItem> itemsToBeRemoved = new ArrayList<CartItem>();
        for (CartItem cartItem : cartItems) {
            if (checked.get(cartItem.getProductName())) {
                bsController.deleteCartItem(obtainCurrentShoppingCartSubsystem(), cartItem.getProductName());
                itemsToBeRemoved.add(cartItem);
            }
        }
        cartItems.removeAll(itemsToBeRemoved);
        checked.clear();
    }

    public void makeItemEditable(CartItem itemToEdit) {
        for (CartItem items : cartItems) {
            if (items.getProductName().equals(itemToEdit.getProductName())) {
                // this.editableItem=true;
                checkedForEdit.remove(itemToEdit.getProductName());
                checkedForEdit.put(itemToEdit.getProductName(), true);
            }
        }
    }

    public void saveEditedItem(CartItem itemTobesaved, String quantity) {
        checkedForEdit.clear();
        for (CartItem items : cartItems) {
            //set the new quantity
            if (items.getProductName().equals(itemTobesaved.getProductName())) {
                try {
                    bsController.runRulesOnQuantity(productName, quantity);
                    items.setQuantity(quantity);
                    //make the items not editable
                    checkedForEdit.put(items.getProductName(), false);
                } catch (RuleException ex) {
                    LOG.log(Level.WARNING, "A RulesException was thrown: {0}", ex.getMessage());
                    MessagesUtil.displayError(ex.getMessage());
                } catch (BusinessException ex) {
                    LOG.log(Level.WARNING, "A BusinessException was thrown: {0}", ex.getMessage());
                    MessagesUtil.displayError(ex.getMessage());
                }
            } else {
                //make the items not editable
                checkedForEdit.put(items.getProductName(), false);
            }
        }
        //iterate through the editablecartItem hashMap and make them uneditable
        checkedForEdit.clear();
    }

    //RULES
    public void validateQuantity(FacesContext context, UIComponent toValidate, Object val) {
        String desired = (String) val;
        try {
            bsController.runRulesOnQuantity(productName, desired);
        } catch (RuleException e) {
            LOG.log(Level.WARNING, "A RuleException was thrown: {0}", e.getMessage());
            ((UIInput) toValidate).setValid(false);
            String msg = e.getMessage();
            FacesMessage retval = new FacesMessage(msg);
            context.addMessage(toValidate.getClientId(context), retval);
        } catch (BusinessException e) {
            LOG.log(Level.WARNING, "A BusinessException was thrown: {0}", e.getMessage());
            ConfigurableNavigationHandler handler = (ConfigurableNavigationHandler) context.getApplication().getNavigationHandler();
            handler.performNavigation("errorDb");
        }
    }

    public SaveCartCallback getSaveCartCallback() {
        return new SaveCartCallback();
    }

    public RetrieveCartCallback getRetrieveCartCallback() {
        return new RetrieveCartCallback();
    }

    private ShoppingCartSubsystem obtainCurrentShoppingCartSubsystem() {
        if (sessionContext.getCust() != null) {
            return sessionContext.getCust().getShoppingCart();
        }
        return sessionContext.getExternalShopCartSS();
    }

    //support function
    private boolean noItemsSelectedForDelete() {
        if (cartItems == null || cartItems.isEmpty()) {
            return true;
        }
        int count = 0;
        for (CartItem item : cartItems) {
            Boolean checkedValue = checked.get(item.getProductName());
            if (checkedValue != null && checkedValue.equals(Boolean.TRUE)) {
                return false;
            }
        }
        return true;
    }

    public class SaveCartCallback
            implements Callback {

        public final Requirement REQUIREMENT = Requirement.SAVE_CART;

        @Override
        public String doUpdate() {
            saveCart(REQUIREMENT);
            MessagesUtil.displaySuccess("Cart successfully saved.");
            return null;
        }
    }

    public class RetrieveCartCallback
            implements Callback {

        public final Requirement REQUIREMENT = Requirement.RETRIEVE_SAVED_CART;

        @Override
        public String doUpdate() {
            retrieveSavedCart(REQUIREMENT);

            //No special page to return
            return null;
        }
    }

    private boolean alreadyAdded;
    private String newPageName = null;//for navigation
    private String catalog;
    private String productName;
    private String quantityRequested;
    private String total;
    private int numberOfItems;
    @Inject
    private SessionData sessionContext;

    // = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();
    // ProductSubsystem stubProdSS = new MPTestProductSubsystemFacade();
    // private String greeting;
    // private String totalShoppingCost;
    private Product product;
    private List<CartItem> cartItems;
    private boolean editableItem;
    //  private String itemUnitPrice;
    private BrowseSelectController bsController = new BrowseSelectController();
    //Items selected by user for deletion
    private HashMap<String, Boolean> checked = new HashMap<String, Boolean>();
    private HashMap<String, Boolean> checkedForEdit = new HashMap<String, Boolean>();
    private static final Logger LOG = Logger.getLogger(BrowseSelectPCB.class.getName());
}
/**
 * @return the cartItemReqCapture
 */
