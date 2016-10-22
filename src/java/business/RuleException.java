/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package business;

import business.exceptions.BusinessException;

/**
 *
 * 
 */
public class RuleException
        extends BusinessException {

    public RuleException(String msg) {
        super(msg);
    }
    private static final long serialVersionUID = 3257003267694145848L;
}
