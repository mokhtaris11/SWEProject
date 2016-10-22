/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package business.externalinterfaces;

import middleware.exceptions.DatabaseException;
import middleware.externalinterfaces.DbClass;

/**
 *
 *  Hak
 */
/* Used only for testing DbClassCustomerProfile */
public interface DbClassCustomerProfileForTest
        extends DbClass {

    public CustomerProfile getCustomerProfileForTest();

    public void readCustomerProfile(Integer custId)
            throws DatabaseException;

}
