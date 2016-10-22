/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package business.externalinterfaces;

import business.exceptions.BackendException;
import java.util.List;
import middleware.exceptions.DatabaseException;

/**
 *
 *  mla
 */
public interface DbClassOrderForTest {

    public List<OrderItem> getOrderItems(Integer orderId)
            throws BackendException;

    public Integer addOrderItemsForTest(ShoppingCart shoppingCard, CustomerProfile custProfile)
            throws BackendException;

    public void deleteOrder(int orderId)
            throws BackendException;
}
