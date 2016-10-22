/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package presentation.control;

import java.io.Serializable;
import java.util.EnumMap;
import java.util.Map;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;
import presentation.data.BrowseSelectPCB;
import presentation.data.CheckoutPCB;
import presentation.data.CheckoutPCB.CheckoutCallback;
import presentation.data.ManageProductsPCB;
import presentation.data.Requirement;
import presentation.data.ViewOrderHistoryPCB;

@Named
@SessionScoped
public class CallbackManager
        implements Serializable {

    @Inject
    BrowseSelectPCB bsPCB;
    @Inject
    CheckoutPCB checkoutPCB;
    @Inject
    ManageProductsPCB prodPCB;
    @Inject
    ViewOrderHistoryPCB orderHistPCB;

    boolean initialized = false;

    public Callback getCallback(String s) {
        return callbackMap.get(Requirement.valueOf(s));
    }
    private Map<Requirement, Callback> callbackMap
            = new EnumMap<Requirement, Callback>(Requirement.class);

    //public CallbackManager() {
    //}
    public void init() {
        if (!initialized) {
            callbackMap.put(bsPCB.getRetrieveCartCallback().REQUIREMENT,
                    bsPCB.getRetrieveCartCallback());
            callbackMap.put(bsPCB.getSaveCartCallback().REQUIREMENT, bsPCB.getSaveCartCallback());
            callbackMap.put(checkoutPCB.getCheckoutCallback().REQUIREMENT, checkoutPCB.getCheckoutCallback());
            callbackMap.put(orderHistPCB.getViewOrdersCallback().REQUIREMENT, orderHistPCB.getViewOrdersCallback());
            callbackMap.put(prodPCB.getManageProductsCallback().REQUIREMENT, prodPCB.getManageProductsCallback());
            callbackMap.put(prodPCB.getManageCatalogsCallback().REQUIREMENT, prodPCB.getManageCatalogsCallback());
            initialized = true;
        }
    }
}
