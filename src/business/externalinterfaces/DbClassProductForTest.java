/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package business.externalinterfaces;

import business.exceptions.BackendException;
import business.util.TwoKeyHashMap;
import middleware.externalinterfaces.DbClass;

/**
 *
 *  sRamanathan
 */
/* Used only for testing DbClassProduct */
public interface DbClassProductForTest
        extends DbClass {

    TwoKeyHashMap<Integer, String, Product> refreshProductTable()
            throws BackendException;
}
