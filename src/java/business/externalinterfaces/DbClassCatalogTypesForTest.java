/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package business.externalinterfaces;

import business.exceptions.BackendException;
import middleware.externalinterfaces.DbClass;

/**
 *
 */
public interface DbClassCatalogTypesForTest
        extends DbClass {

    public CatalogTypes getCatalogTypes()
            throws BackendException;
}
