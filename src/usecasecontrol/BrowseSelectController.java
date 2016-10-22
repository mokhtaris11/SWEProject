/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package usecasecontrol;

import business.Quantity;
import business.RulesQuantity;
import business.exceptions.BackendException;
import business.exceptions.BusinessException;
import business.exceptions.RuleException;
import business.externalinterfaces.CartItem;
import business.externalinterfaces.CustomerSubsystem;
import business.externalinterfaces.Product;
import business.externalinterfaces.ProductSubsystem;
import business.externalinterfaces.Rules;
import business.externalinterfaces.ShoppingCart;
import business.externalinterfaces.ShoppingCartSubsystem;
import business.productsubsystem.ProductImpl;
import business.productsubsystem.ProductSubsystemFacade;
import business.util.StringParse;
import java.io.Serializable;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class BrowseSelectController
        implements Serializable {

    ////MORE NEW
    ProductSubsystem pss = new ProductSubsystemFacade();

    public List<String> getCatalogNames()
            throws BackendException {

        return pss.getCatalogNames();
    }

    public List<Product> getProducts(String selectedCatalog)
            throws BackendException {

        return pss.getProductList(selectedCatalog);

    }

    public Product getProduct(String prodName)
            throws BackendException {
        return pss.getProduct(prodName);
    }

    public String getNameOfEditedProd(String catalogtype)
            throws BackendException {
        //IMPLEMENT
        return "stub";

    }

    public String readCartItemProdName(ShoppingCartSubsystem shopCartSS, int pos) {
        return "stub cartitem";
    }

    //run rules on quantity and return the total price 
    public void runRulesOnQuantity(String prodName, String quantityReq)
            throws RuleException, BusinessException {
        Quantity quantity = new Quantity(quantityReq);
        pss.readQuantityAvailable(prodName, quantity);
        RulesQuantity rulesQuantity = new RulesQuantity(quantity);
        rulesQuantity.runRules();
    }

    public String getCartItemTotalPrice(String price, String quantityReq) {
        String totalprice = StringParse.multiplyDoubles(price,
                quantityReq);
        return totalprice;
    }

    public void deleteCartItem(ShoppingCartSubsystem shopCartSS, String itemName) {
        shopCartSS.deleteCartItem(itemName);
    }

    public void deleteCartItem(ShoppingCartSubsystem shopCartSS, Integer pos) {
        shopCartSS.deleteCartItem(pos);
    }

    public void addCartItem(ShoppingCartSubsystem shopCartSS, String name, String quantityReq, String totalprice, Integer positionOfEdit)
            throws BackendException {

        shopCartSS.addCartItem(name, quantityReq, totalprice, positionOfEdit);
        //IShoppingCart cart = shopCartSS.getLiveCart();
    }

    public void addCartItem(ShoppingCartSubsystem shopCartSS, String productName, int quantity)
            throws BackendException {
        Product product = getProduct(productName);
        String quantityReq = (new Integer(quantity)).toString();
        double totalPrice = Double.parseDouble(product.getUnitPrice()) * quantity;
        String totalPriceStr = (new Double(totalPrice)).toString();
        addCartItem(shopCartSS, productName, quantityReq, totalPriceStr, null);
    }

    public String getLiveCartTotalCost(ShoppingCartSubsystem shopCartSS) {

        String total = String.valueOf(shopCartSS.getLiveCart().getTotalPrice());
        return total;
    }

    public boolean removeItemFromCart(ShoppingCartSubsystem shopCartSS, String item, int pos) {

        LOG.log(Level.INFO, "To be deleted: {0} at position {1}", new Object[]{item, pos});
        boolean itemDeleted = shopCartSS.deleteCartItem(item);
        return itemDeleted;
    }

    public void addToCart(Product product, int quantity) {
    }

    public List<CartItem> getCartItems(ShoppingCartSubsystem shopCartSS) {
        return shopCartSS.getLiveCartItems();
    }

    public void saveCart(CustomerSubsystem cust)
            throws BackendException {
        cust.saveShoppingCart();
    }

    public ShoppingCartSubsystem obtainCurrentShoppingCartSubsystem(CustomerSubsystem cust,
            ShoppingCartSubsystem extCartSS) {
        if (cust == null) {
            return extCartSS;
        } else {
            return cust.getShoppingCart();
        }
    }

    public List<CartItem> retrieveSavedCart(CustomerSubsystem cust) {
        ShoppingCartSubsystem ss = cust.getShoppingCart();
        ss.makeSavedCartLive();
        return ss.getLiveCartItems();
    }
    /////OLD
    private static final Logger LOG = Logger
            .getLogger("BrowseSelectController.class.getName()");

    // ///////// EVENT HANDLERS -- new code goes here ////////////
    // control of mainFrame
	/*
     class PurchaseOnlineActionListener implements ActionListener {
     public void actionPerformed(ActionEvent e) {
     LOG.info("User Home: " + System.getProperty("user.home"));
     catalogListWindow = new CatalogListWindow();
     IProductSubsystem pss = new ProductSubsystemFacade();

     try {
     List<String[]> catNames = pss.getCatalogNames();
     catalogListWindow.updateModel(catNames);
     mainFrame.getDesktop().add(catalogListWindow);
     catalogListWindow.setVisible(true);
     } catch (DatabaseException ex) {
     String errMsg = "A database problem is preventing further processing. "
     + "Technical spec: " + ex.getMessage();
     JOptionPane.showMessageDialog(mainFrame, errMsg, "Error",
     JOptionPane.ERROR_MESSAGE);
     }			
     }
     }*/
    /*
     class LoginListener implements ActionListener {
     public void actionPerformed(ActionEvent e) {
     LOG.info("HELP");
     try {
     LoginControl loginControl =  new LoginControl(mainFrame, mainFrame);
     loginControl.startLogin();
     } catch(Exception ex) {
     ex.printStackTrace();
     }
     }
     }*/
    /*
     class RetrieveCartActionListener implements ActionListener, Controller {
     public void doUpdate() {
     SessionContext ctx = SessionContext.getInstance();
     ICustomerSubsystem cust = (ICustomerSubsystem) ctx
     .get(CustomerConstants.CUSTOMER);
     IShoppingCartSubsystem shopCartSS = cust.getShoppingCart();
			
     // Saved cart was retrieved during login
     shopCartSS.makeSavedCartLive();
     List<String[]> displayableListOfItems = ShoppingCartUtil
     .makeDisplayableList(shopCartSS.getLiveCartItems());

     cartItemsWindow.updateModel(displayableListOfItems);
     String totalPrice = "0.00";
     if(shopCartSS.getLiveCart() != null) {
     totalPrice = String.valueOf(shopCartSS.getLiveCart()
     .getTotalPrice());
     }
     LOG.info("totalPrice = " + totalPrice);
     LOG.info("cartItemsWindow is null? " + (cartItemsWindow == null));
     cartItemsWindow.setTotal(totalPrice);

			
     EbazaarMainFrame.getInstance().getDesktop().add(cartItemsWindow);
     cartItemsWindow.setVisible(true);
     }

     public void actionPerformed(ActionEvent e) {
     cartItemsWindow = new CartItemsWindow();
     SessionContext ctx = SessionContext.getInstance();
     Boolean loggedIn = (Boolean) ctx.get(CustomerConstants.LOGGED_IN);
     if (!loggedIn.booleanValue()) {
     LoginControl loginControl = new LoginControl(cartItemsWindow,
     mainFrame, this);
     loginControl.startLogin();
     } else {
     doUpdate();
     }
     }
     }*/
    // control of CatalogListWindow
	/*class SelectCatalogListener implements ActionListener {
     public void actionPerformed(ActionEvent evt) {
     JTable table = catalogListWindow.getTable();
     int selectedRow = table.getSelectedRow();
     if (selectedRow >= 0) {
     try {
     String type = (String) table.getValueAt(selectedRow, 0);
     IProductSubsystem prodSubsystem = new ProductSubsystemFacade();
     List<String[]> displayableProducts = ProductUtil
     .extractProductNames(prodSubsystem
     .getProductList(type));
					
     productListWindow = new ProductListWindow(type);
     productListWindow.updateModel(displayableProducts);
     } catch (DatabaseException e) {
     String errMsg = "Database is unavailable. Technical message: " + 
     e.getMessage();
     JOptionPane.showMessageDialog(catalogListWindow, errMsg,
     "Error", JOptionPane.ERROR_MESSAGE);
     }
     catalogListWindow.setVisible(false);
     mainFrame.getDesktop().add(productListWindow);
     productListWindow.setVisible(true);
     }
     // If arriving here, the value of selectedRow is -1, 
     // which means no row was selected
     else {
     String errMsg = "Please select a row.";
     JOptionPane.showMessageDialog(catalogListWindow, errMsg,
     "Error", JOptionPane.ERROR_MESSAGE);
     }
     }

     }*/

    /*class BackToMainFrameListener implements ActionListener {
     public void actionPerformed(ActionEvent evt) {
     mainFrame.setVisible(true);
     catalogListWindow.setVisible(false);
     }
     }*/
    // Control of ProductListWindow
	/*class SelectProductListener implements ActionListener {
     HashMap<String, String[]> readProductDetailsData() {
     DefaultData productData = DefaultData.getInstance();
     return productData.getProductDetailsData();
     }
     boolean useDefaultData;
     public SelectProductListener(boolean useDefData) {
			
     useDefaultData = useDefData;
     } */
    /* Returns, as a String array, the product details based on the type */
    /*	String[] readProductDetailsData(String type) {
			
     if (useDefaultData) {
     DefaultData productData = DefaultData.getInstance();
     return productData.getProductDetailsData(type);
     } else {
     try {
     IProductSubsystem prodSubsys = new ProductSubsystemFacade();
     IProductFromDb product = prodSubsys.getProduct(type);				
     String[] prodInfo = ProductUtil
     .extractProdInfoForCust(product);
     return prodInfo;
				
     } catch (DatabaseException e) {
     String errMsg = "A database error has occurred. Technical message: " + e.getMessage();
     JOptionPane.showMessageDialog(catalogListWindow, errMsg,
     "Error", JOptionPane.ERROR_MESSAGE);
     return null;
     }
     }
     }

     public void actionPerformed(ActionEvent evt) {
     JTable table = productListWindow.getTable();
     int selectedRow = table.getSelectedRow();

     if (selectedRow >= 0) {
     String type = (String) table.getValueAt(selectedRow, 0);
     String[] productParams = readProductDetailsData(type);
     productDetailsWindow = new ProductDetailsWindow(productParams);			
     mainFrame.getDesktop().add(productDetailsWindow);
     productListWindow.setVisible(false);
     productDetailsWindow.setVisible(true);
     }
     // Value of selectedRow is -1, which means no row was selected
     else {
     String errMsg = "Please select a row.";
     JOptionPane.showMessageDialog(productListWindow, errMsg,
     "Error", JOptionPane.ERROR_MESSAGE);
     }
     }
     }*/

    /*class BackToCatalogListListener implements ActionListener {
     public void actionPerformed(ActionEvent evt) {
     catalogListWindow.setVisible(true);
     productListWindow.setVisible(false);
     }
     }*/
    /////// control ProductDetails
/*	class AddEditCartItemListener implements ActionListener {
     private boolean editMode = false;
     AddEditCartItemListener(boolean edit) {
     this.editMode = edit;
     }
     public void actionPerformed(ActionEvent evt) {
     if (editMode) {
     JTable table = cartItemsWindow.getTable();
				
     int selectedRow = table.getSelectedRow();
     if (selectedRow >= 0) {
     String type = (String) table.getValueAt(selectedRow, 0);
     LOG.info("edit -- " + type);
					
					
     ProductSubsystemFacade productData = new ProductSubsystemFacade();
     try {					
     //Logging info
     IProductSubsystem prodSubsys = new ProductSubsystemFacade();
     String productName = prodSubsys.getProduct(type).getProductName();
						
     LOG.info("Editing values for " + productName + " in shopping cart.");
						
     //End logging
     quantityWindow = new QuantityWindow(true, selectedRow);
     EbazaarMainFrame.getInstance().getDesktop().add(quantityWindow);
     quantityWindow.setVisible(true);
     } catch (DatabaseException e) {
     String errMsg = "A database error has occurred. Technical message: " + e.getMessage();
     JOptionPane.showMessageDialog(catalogListWindow, errMsg,
     "Error", JOptionPane.ERROR_MESSAGE);
     }
     } else {
     String msg = "Please select an item to edit";
     JOptionPane.showMessageDialog(productListWindow, msg,
     "Message", JOptionPane.INFORMATION_MESSAGE);
     }
     } else {
     productDetailsWindow.setVisible(false);
     quantityWindow = new QuantityWindow(false, null);
     EbazaarMainFrame.getInstance().getDesktop().add(quantityWindow);
     quantityWindow.setVisible(true);			
     }
     quantityWindow.setParentWindow(productDetailsWindow);
     }
     }*/
    /*class DeleteCartItemListener implements ActionListener {
     public void actionPerformed(ActionEvent evt) {
     String successMsg = "Item deleted successfully.";
     String failMsg = "Unable to delete item as requested.";
     JTable table = cartItemsWindow.getTable();
     ///////
     BrowseSelectData data = BrowseSelectData.getInstance();
			
     int selectedRow = table.getSelectedRow();
			
     if (selectedRow >= 0) {
				
     String item = (String) table.getValueAt(selectedRow, 0);
				
     LOG.info("To be deleted: " + item + " at position " + selectedRow);

     IShoppingCartSubsystem sc = ShoppingCartSubsystemFacade.getInstance();
					
     //boolean xx=sc.deleteCartItem(selectedRow);
     if(sc.deleteCartItem(selectedRow)) {
					
     List<ICartItem> ht = sc.getLiveCartItems();
					
					
     LOG.info("Retrieved " + ht.size() + " items from Live Cart");
					
     List<String[]> newlist = ShoppingCartUtil.cartItemsToStringArrays(ht);
     ///////
     //List<String[]> newlist=data.getCartItemsAfterItemDelet(JTable table);
					
     if (cartItemsWindow != null) {
     cartItemsWindow.dispose();

     }
     cartItemsWindow = new CartItemsWindow();
     EbazaarMainFrame.getInstance().getDesktop()
     .add(cartItemsWindow);
     //NEW LIST AFTER DELETE 
     cartItemsWindow.updateModel(newlist);
     //NEW TOTAL WE HAVE A METHOD FOR THIS ALREADY 
     cartItemsWindow.setTotal(String.valueOf(sc.getLiveCart()
     .getTotalPrice()));
     cartItemsWindow.setVisible(true);
					
     //Pause for a moment
     try {
     Thread.sleep(500);
     } catch (InterruptedException e) {					
     //Do nothing
     }			
     JOptionPane.showMessageDialog(cartItemsWindow, successMsg,
     "Message", JOptionPane.INFORMATION_MESSAGE);
     }*/
    /*
     if (sc.deleteCartItem(item)) {
     List<ICartItem> ht = sc.getLiveCartItems();
     LOG.info("Retrieved " + ht.size() + " items from Live Cart");
     List<String[]> newlist = ShoppingCartUtil.cartItemsToStringArrays(ht);
     if (cartItemsWindow != null) {
     cartItemsWindow.dispose();
					
     }
     cartItemsWindow = new CartItemsWindow();
     EbazaarMainFrame.getInstance().getDesktop()
     .add(cartItemsWindow);
     cartItemsWindow.updateModel(newlist);
     cartItemsWindow.setTotal(String.valueOf(sc.getLiveCart()
     .getTotalPrice()));
     cartItemsWindow.setVisible(true);
										
     //Pause for a moment
     try {
     Thread.sleep(1000);
     } catch (InterruptedException e) {					
     //Do nothing
     }			
     JOptionPane.showMessageDialog(cartItemsWindow, successMsg,
     "Message", JOptionPane.INFORMATION_MESSAGE);
     } */
    /*else {			
     JOptionPane.showMessageDialog(cartItemsWindow, failMsg,
     "Message", JOptionPane.INFORMATION_MESSAGE);
     }

     }
     // value of selectedRow is -1, which means no row was selected
     else {
     String errMsg = "Please select an item to delete.";
     JOptionPane.showMessageDialog(cartItemsWindow, errMsg, "Error",
     JOptionPane.ERROR_MESSAGE);

     }
     }

     }*/
    /*class QuantityOkListener implements ActionListener {
		
     private boolean editMode = false;
     private Integer positionOfEdit = null;
	    
     QuantityOkListener(boolean edit, Integer posOfEdit) {
     this.editMode = edit;
     positionOfEdit = posOfEdit;
     }
		
     /*IProductSubsystem prodSS = new ProductSubsystemFacade();
     IShoppingCartSubsystem shopCartSS = ShoppingCartSubsystemFacade.getInstance();
     IShoppingCart cart = shopCartSS.getLiveCart();*/
    /*	public void actionPerformed(ActionEvent evt) {
     String name = null;
     String price = "0.0";
     //gather name and price in different ways for add and edit
     if(editMode) { //find name and price from corresponding position in list of cartItems
     BrowseSelectData data = BrowseSelectData.getInstance();	
     name=data.ReadCartItemProdName(positionOfEdit);
     /*ICartItem item = cart.getCartItems().get(positionOfEdit);
     name = item.getProductName();*/
    /*try {
     //price = prodSS.getProduct(name).getUnitPrice();
     price=data.ReadCartItemUnitPrice(name);
     } catch(DatabaseException e) {
     String errMsg = "The database is unavailable. Please try again later.";
     JOptionPane.showMessageDialog(cartItemsWindow, errMsg, "Error",
     JOptionPane.ERROR_MESSAGE);
     }
     }
     else { //find name and price from the previous productDetailsWindow
     name = productDetailsWindow.getItem();
     price = String.valueOf(productDetailsWindow.getPrice());
     }*/
    /*	try {
				
				
     String quantityReq = quantityWindow.getQuantityDesired();
				
     //Quantity quantity = new Quantity(quantityReq); //system call should be moved 
				
						
     // this sets the quantity available in the Quantity object
     //prodSS.readQuantityAvailable(name, quantity);///
     // do rules
     //IRules transferObject = new RulesQuantity(quantity);//
     //transferObject.runRules();//
     // if ok, proceed

     //String totalprice = StringParse.multiplyDoubles(price,
     //	quantityReq); //
     runRulesOnQuantity(name,quantityReq);
     //String totalprice=data.getCartItemTotalPrice(price,name);
				
     if (editMode) {
     //shopCartSS.deleteCartItem(positionOfEdit);
     //data.deleteCartItem(positionOfEdit);
     cartItemsWindow.setVisible(false);
     }
     //shopCartSS.addCartItem(name, quantityReq, totalprice, positionOfEdit);
     //data.addCartItem(name, quantityReq, totalprice, positionOfEdit);
				
     //List<String[]> displayableListOfItems = ShoppingCartUtil
     //.makeDisplayableList(shopCartSS.getLiveCartItems());
     //	List<String[]> displayableListOfItems=data.displayableListOfItems();
				
     quantityWindow.dispose();
     productDetailsWindow.setVisible(false);
     cartItemsWindow = new CartItemsWindow();
				
     //String total = data.getLiveCartTotalCost();
				
     //cartItemsWindow.setTotal(total);
     /*
     if(editMode) {
     StringParse.swap(displayableListOfItems, displayableListOfItems.size() - 1, 
     positionOfEdit.intValue());
     }*/
    //cartItemsWindow.updateModel(displayableListOfItems);
    /*	EbazaarMainFrame.getInstance().getDesktop().add(cartItemsWindow);
     cartItemsWindow.setVisible(true);
     }

     catch (RuleException e) {
     JOptionPane.showMessageDialog(catalogListWindow,
     e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);

     } catch (DatabaseException e) {
     String errMsg = "Database is unavailable.";
     JOptionPane.showMessageDialog(catalogListWindow, errMsg,
     "Error", JOptionPane.ERROR_MESSAGE);

     } catch (EBazaarException e) {
     String errMsg = "An error has occurred that prevents further processing.";
     JOptionPane.showMessageDialog(catalogListWindow, errMsg,
     "Error", JOptionPane.ERROR_MESSAGE);
     }
     }
     }*/

    /*class BackToProductListListener implements ActionListener {
     public void actionPerformed(ActionEvent evt) {
     productDetailsWindow.setVisible(false);
     productListWindow.setVisible(true);
     }
     }*/
    // /// control CartItemsWindow

    /*class ContinueShoppingListener implements ActionListener {
		
     public void actionPerformed(ActionEvent evt) {
     cartItemsWindow.setVisible(false);

     // user has been looking at this product list
     if (productListWindow != null) {
     productListWindow.setVisible(true);
     }
     // user has just retrieved saved cart
     else {

     if (catalogListWindow != null) {
     catalogListWindow.dispose();
     }
     catalogListWindow = new CatalogListWindow();
     ProductSubsystemFacade prodSS = new ProductSubsystemFacade();

     List<String[]> newlist = null;
     try {
					
     newlist = prodSS.getCatalogNames();
					
					
     catalogListWindow.updateModel(newlist);
     } catch (DatabaseException e) {
     String errMsg = "Database is unavailable.";
     JOptionPane.showMessageDialog(catalogListWindow, errMsg,
     "Error", JOptionPane.ERROR_MESSAGE);

     }

     EbazaarMainFrame.getInstance().getDesktop()
     .add(catalogListWindow);
     catalogListWindow.setVisible(true);
     }
     }
     }

     class SaveCartListener implements ActionListener {
     public void actionPerformed(ActionEvent evt) {
     // implement
     // here's the logic:
     // require login if not logged in
     // if current live cart does not have a cartid
     // then it's new and we need to get next avail id
     // then save cart level stuff, then loop
     // and get lineitemid's for each line, and save each
     // line
     // if current cart does have an id, we just save
     // cart items that are not flagged as "hasBeenSaved"
     // (a boolean in that class)
     // no need to save at the cart level, just save the
     // not-so-far-saved cart items
     // postcondition: the live cart has a cartid
     // and all cart items are flagged as "hasBeenSaved"

     }
     }

     // /////// PUBLIC INTERFACE -- for getting instances of listeners ///
     // EbazaarMainFrame
     /*
     public ActionListener getNewOnlinePurchaseListener(EbazaarMainFrame f) {
     return (new PurchaseOnlineActionListener());
     }*/
    /*
     public LoginListener getLoginListener(EbazaarMainFrame f) {
     return new LoginListener();
     }

     public ActionListener getRetrieveCartActionListener(EbazaarMainFrame f) {
     return (new RetrieveCartActionListener());
     }*/
    // CatalogListWindow
	/*public ActionListener getSelectCatalogListener(CatalogListWindow w) {
     return new SelectCatalogListener();
     }*/

    /*public ActionListener getBackToMainFrameListener(CatalogListWindow w) {
     return new BackToMainFrameListener();
     }*/
    // ProductListWindow
	/*public ActionListener getSelectProductListener(ProductListWindow w,
     boolean useDefData) {
     return new SelectProductListener(useDefData);
     }*/

    /*public ActionListener getBackToCatalogListListener(ProductListWindow w) {
     return new BackToCatalogListListener();
     }*/
    // ProductDetails Window
    /*public ActionListener getAddToCartListener(ProductDetailsWindow w) {
     return new AddEditCartItemListener(false);
     }*/

    /*public ActionListener getEditCartListener(CartItemsWindow w) {
     return new AddEditCartItemListener(true);
     }*/

    /*public ActionListener getDeleteCartListener(CartItemsWindow w) {
     return new DeleteCartItemListener();
     }*/

    /*public ActionListener getBackToProductListListener(ProductDetailsWindow w) {
     return new BackToProductListListener();
     }*/
    // CartItemsWindow

    /*public ActionListener getContinueShoppingListener(CartItemsWindow w) {
     return (new ContinueShoppingListener());
     }*/

    /*public ActionListener getSaveCartListener(CartItemsWindow w) {
     return (new SaveCartListener());
     }*/

    /*public ActionListener getQuantityOkListener(QuantityWindow w, boolean edit, Integer posOfEdit) {
     return new QuantityOkListener(edit, posOfEdit);
     }*/
    // ////// PUBLIC ACCESSORS to register screens controlled by this class////
/*
     public void setCatalogList(CatalogListWindow w) {
     catalogListWindow = w;
     }

     public void setMainFrame(EbazaarMainFrame m) {
     mainFrame = m;
     }

     public void setProductListWindow(ProductListWindow p) {
     productListWindow = p;
     }

     public void setProductDetailsWindow(ProductDetailsWindow p) {
     productDetailsWindow = p;
     }

     public void setCartItemsWindow(CartItemsWindow w) {
     cartItemsWindow = w;
     }

     public void setSelectOrderWindow(SelectOrderWindow w) {
     selectOrderWindow = w;
     }

     public void setMaintainCatalogTypes(MaintainCatalogTypes w) {
     maintainCatalogTypes = w;
     }

     public void setMaintainProductCatalog(MaintainProductCatalog w) {
     maintainProductCatalog = w;
     }

     public void setQuantityWindow(QuantityWindow w) {
     quantityWindow = w;
     }

     //private boolean edit;
	
     /////// screens -- private references
     private EbazaarMainFrame mainFrame;
     private ProductListWindow productListWindow;
     private CatalogListWindow catalogListWindow;
     private ProductDetailsWindow productDetailsWindow;
     private CartItemsWindow cartItemsWindow;
     private SelectOrderWindow selectOrderWindow;
     private MaintainCatalogTypes maintainCatalogTypes;
     private MaintainProductCatalog maintainProductCatalog;
     private QuantityWindow quantityWindow;
     private Window[] allWindows = {};
     private JInternalFrame[] internalFrames = { productListWindow,
     catalogListWindow, productDetailsWindow, quantityWindow, cartItemsWindow,
     selectOrderWindow, maintainCatalogTypes, maintainProductCatalog };

     public void cleanUp() {
     ApplicationUtil.cleanup(allWindows);
     ApplicationUtil.cleanup(internalFrames);
     }*/
    public BrowseSelectController() {

    }
}
