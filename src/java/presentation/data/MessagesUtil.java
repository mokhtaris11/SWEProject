/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package presentation.data;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

/**
 *
 * 
 */
public class MessagesUtil {

    public static final String GENERAL_ERR_MSG
            = "An error has occurred that prevents further processing.";
    public static final String SELECT_ROW_MSG
            = "Please select a row.";
    public static final String NOT_AUTHORIZED
            = "You are not authorized to access this page.";
    public static final String DATABASE_ERROR
            = "A database error has occurred. Please try again later.";

    public static void displaySuccess(String message) {
        FacesContext context = FacesContext.getCurrentInstance();
        FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, message, null);
        context.addMessage(null, msg);
    }

    public static void displayError(String message) {
        FacesContext context = FacesContext.getCurrentInstance();
        FacesMessage errMsg = new FacesMessage(FacesMessage.SEVERITY_ERROR, message, null);
        context.addMessage(null, errMsg);
    }
}
