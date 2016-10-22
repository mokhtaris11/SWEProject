package usecasecontrol;

import java.util.logging.Logger;

import middleware.externalinterfaces.Cleanup;
import business.BusinessCleanup;

public class ApplicationCleanup
        implements Cleanup {

    Cleanup bc;
    private static final Logger LOG = Logger.getLogger(ApplicationCleanup.class.getName());

    @Override
    public void cleanup() {
        LOG.info("Disposing of all open windows...");
        for (CleanupControl c : allCleanupControllers) {
            c.cleanUp();
        }
        bc = new BusinessCleanup();
        bc.cleanup();
    }
    private CleanupControl[] allCleanupControllers
            = { //BrowseAndSelectController.getInstance(),
            //CheckoutController.getInstance(),
            //ManageProductsController.getInstance(),
            //ViewOrdersController.getInstance()
            };

}
