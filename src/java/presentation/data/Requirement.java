/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package presentation.data;

/**
 * A Requirement is an intended action by the user. Typically corresponds to a target page, but not always
 * (for instance, SAVE_CART).
 */
public enum Requirement {

    RETRIEVE_SAVED_CART, SAVE_CART, CHECKOUT, MANAGE_CATALOGS, MANAGE_PRODUCTS, VIEW_ORDER_HISTORY;
}
