package middleware;

import middleware.dataaccess.DataAccessSubsystemFacade;
import middleware.externalinterfaces.Cleanup;
import middleware.externalinterfaces.DataAccessSubsystem;

public class MiddlewareCleanup
        implements Cleanup {

    @Override
    public void cleanup() {
        //release database connections
        DataAccessSubsystem dass = new DataAccessSubsystemFacade();
        dass.closeAllConnections(this);
    }
}
