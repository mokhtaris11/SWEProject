package presentation.control;

import business.exceptions.UnauthorizedException;
import java.util.EnumMap;
import java.util.Map;
import presentation.data.MessagesUtil;
import presentation.data.Requirement;

public class Authorization {

    private static Map<Requirement, Boolean> accessControl
            = new EnumMap<Requirement, Boolean>(Requirement.class);

    static {
        accessControl.put(Requirement.CHECKOUT, false);
        accessControl.put(Requirement.RETRIEVE_SAVED_CART, false);
        accessControl.put(Requirement.SAVE_CART, false);
        accessControl.put(Requirement.VIEW_ORDER_HISTORY, false);
        accessControl.put(Requirement.MANAGE_CATALOGS, true);
        accessControl.put(Requirement.MANAGE_PRODUCTS, true);
    }

    private static boolean requiresAdmin(Requirement requirement) {
        if (requirement == null) {
            return false;
        }

        if (!accessControl.containsKey(requirement)) {
            return false;
        }
        return accessControl.get(requirement);
    }

    public static void checkAuthorization(Requirement r, boolean custIsAdmin)
            throws UnauthorizedException {
        boolean requiresAdmin = requiresAdmin(r);
        if (requiresAdmin && !custIsAdmin) {
            throw new UnauthorizedException(MessagesUtil.NOT_AUTHORIZED);
        }
    }

}
