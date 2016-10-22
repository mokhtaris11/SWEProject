/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package presentation.data;

import business.externalinterfaces.CustomerSubsystem;
import business.externalinterfaces.ShoppingCartSubsystem;
import java.io.Serializable;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;

@Named("sessionContext")
@SessionScoped
public class SessionData
        implements Serializable {

    private CustomerSubsystem cust;
    private ShoppingCartSubsystem shopCartSS;
    @Inject
    private ShoppingCartSubsystem externalShopCartSS;
    private boolean isLoggedIn = false;

    public boolean custIsAdmin() {
        if (cust == null) {
            return false;
        }
        return cust.isAdmin();
    }

    public ShoppingCartSubsystem getExternalShopCartSS() {
        return externalShopCartSS;
    }

    public void clearExternalShopCart() {
        externalShopCartSS = null;
    }

    public void setCust(CustomerSubsystem cust) {
        this.cust = cust;
    }

    public void setShopCartSS(ShoppingCartSubsystem shopCartSS) {
        this.shopCartSS = shopCartSS;
    }

    public CustomerSubsystem getCust() {
        return cust;
    }

    public ShoppingCartSubsystem getShopCartSS() {
        return shopCartSS;
    }

    public void setExternalShopCartSS(ShoppingCartSubsystem externalShopCart) {
        this.externalShopCartSS = externalShopCart;
    }

    public boolean getIsLoggedIn() {
        return isLoggedIn;
    }

    public void setIsLoggedIn(boolean b) {
        isLoggedIn = b;
    }

    public SessionData() {

    }

}
