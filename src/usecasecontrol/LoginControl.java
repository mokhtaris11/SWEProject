package usecasecontrol;

import business.DbClassLogin;
import business.Login;
import business.customersubsystem.CustomerSubsystemFacade;
import business.exceptions.BackendException;
import business.exceptions.UserException;
import business.externalinterfaces.CustomerSubsystem;

public class LoginControl {

    public int authenticate(Login login)
            throws UserException, BackendException {

        DbClassLogin dbClass = new DbClassLogin(login);
        if (!dbClass.authenticate()) {
            throw new UserException("Authentication failed for ID: " + login.getCustId());
        }
        return dbClass.getAuthorizationLevel();

    }

    public CustomerSubsystem prepareCustomerObject(Integer custId, int authorizationLevel)
            throws BackendException {
        CustomerSubsystem customer = new CustomerSubsystemFacade();
        customer.initializeCustomer(custId, authorizationLevel);
        return customer;
    }

}
