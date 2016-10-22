package usecasecontrol;

import business.exceptions.BackendException;
import business.exceptions.BusinessException;
import business.exceptions.RuleException;
import business.externalinterfaces.Address;
import business.externalinterfaces.CreditCard;
import business.externalinterfaces.CustomerProfile;
import business.externalinterfaces.CustomerSubsystem;
import business.externalinterfaces.ShoppingCartSubsystem;
import business.shoppingcartsubsystem.ShoppingCartSubsystemFacade;
import java.util.List;
import java.util.logging.Logger;

public class CheckoutController {

    private static final Logger LOG = Logger.getLogger(CheckoutController.class
            .getPackage().getName());

    public CustomerProfile getCustomerProfile(CustomerSubsystem cust) {
        return cust.getCustomerProfile();
    }

    public List<Address> getSavedAddresses(CustomerSubsystem cust)
            throws BackendException {
        return cust.getAllAddresses();
    }

    public CreditCard getDefaultCreditCard(CustomerSubsystem cust) {
        return cust.getDefaultPaymentInfo();
    }

    public Address getDefaultShippingAddress(CustomerSubsystem cust) {
        return cust.getDefaultShippingAddress();
    }

    public Address getDefaultBillingAddress(CustomerSubsystem cust) {
        return cust.getDefaultBillingAddress();
    }

    public void runShoppingCartRules(ShoppingCartSubsystem shopCartSs)
            throws RuleException, BusinessException {
        shopCartSs.runShoppingCartRules();

    }

    public void runPaymentRules(CustomerSubsystem cust, Address addr, CreditCard cc)
            throws RuleException, BusinessException {
        //ask Cust to run rules
        cust.runPaymentRules(addr, cc);
    }

    public Address runAddressRules(CustomerSubsystem cust, Address addr)
            throws RuleException, BusinessException {
        return cust.runAddressRules(addr);
    }

    public void runFinalOrderRules(ShoppingCartSubsystem scss)
            throws RuleException, BusinessException {
        scss.runFinalOrderRules();
    }

    public void setAddressesInCart(CustomerSubsystem cust, Address shipAddress, Address billAddress) {
        cust.setShippingAddressInCart(shipAddress);
        cust.setBillingAddressInCart(billAddress);
    }

    public void setPaymentMethodInCart(CustomerSubsystem cust, CreditCard cc) {
        cust.setPaymentInfoInCart(cc);
    }

    public void verifyCreditCart(CustomerSubsystem cust)
            throws BusinessException {
        cust.checkCreditCard();
    }

    public void submitFinalOrder(CustomerSubsystem cust)
            throws BackendException {
        cust.submitOrder();
        cust.getShoppingCart().deleteSavedCart(cust.getShoppingCart().getLiveCart().getCartId());
    }

    public void saveNewAddress(CustomerSubsystem cust, Address addr)
            throws BackendException {
        cust.saveNewAddress(addr);
    }

	// ///////// EVENT HANDLERS -- new code goes here ////////////
    // /// control CartItemsWindow
	/*
     class ProceedToCheckoutListener implements ActionListener, Callback {

     public void doUpdate() {
     populateScreen();
     }

		
     void populateScreen() {
     SessionContext context = SessionContext.getInstance();
     CustomerSubsystem cust = (CustomerSubsystem) context
     .get(CustomerConstants.CUSTOMER);
     CustomerProfile custProfile = cust.getCustomerProfile();
     Address defaultShipAddress = cust.getDefaultShippingAddress();
     Address defaultBillAddress = cust.getDefaultBillingAddress();
     shippingBillingWindow = new ShippingBillingWindow();
     shippingBillingWindow.setShippingAddress(custProfile.getFirstName()
     + " " + custProfile.getLastName(),
     defaultShipAddress.getStreet1(),
     defaultShipAddress.getCity(),
     defaultShipAddress.getState(), defaultShipAddress.getZip());
     shippingBillingWindow.setBillingAddress(custProfile.getFirstName()
     + " " + custProfile.getLastName(),
     defaultBillAddress.getStreet1(),
     defaultBillAddress.getCity(),
     defaultBillAddress.getState(), defaultBillAddress.getZip());

     EbazaarMainFrame.getInstance().getDesktop()
     .add(shippingBillingWindow);
     shippingBillingWindow.setVisible(true);
     }

     public void actionPerformed(ActionEvent evt) {
     cartItemsWindow.setVisible(false);
			
     ShoppingCartSubsystem shopCartSs = ShoppingCartSubsystemFacade
     .getInstance();
     boolean rulesOk = true;
     try {
     shopCartSs.runShoppingCartRules();
     } catch (RuleException e) {
     rulesOk = false;
     LOG.warning("A RuleException was thrown: " + e.getMessage());
     JOptionPane.showMessageDialog(cartItemsWindow, e.getMessage(),
     "Error", JOptionPane.ERROR_MESSAGE);
     cartItemsWindow.setVisible(true);
     } catch (BusinessException e) {
     rulesOk = false;
     JOptionPane
     .showMessageDialog(
     cartItemsWindow,
     "An error has occurred that prevents further processing",
     "Error", JOptionPane.ERROR_MESSAGE);
     cartItemsWindow.setVisible(true);
     }

     if (rulesOk) {
     SessionContext ctx = SessionContext.getInstance();
     Boolean loggedIn = (Boolean) ctx
     .get(CustomerConstants.LOGGED_IN);
     if (!loggedIn.booleanValue()) {
     shippingBillingWindow = new ShippingBillingWindow();
     LoginUIControl loginControl = new LoginUIControl(
     shippingBillingWindow, cartItemsWindow, this);

     loginControl.startLogin();
     } else {
     populateScreen();
     }
     }
     }
     }*/
    public CheckoutController() {
    }

}
