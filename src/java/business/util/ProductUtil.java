package business.util;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import business.externalinterfaces.Product;

public class ProductUtil {

    public static List<String[]> extractProductNames(List<Product> prodList) {
        final int PROD_NAME = 0;
        List<String[]> returnValue = new LinkedList<String[]>();
        String[] nextArray = null;
        final int SIZE = 1;
        for (Product prod : prodList) {
            nextArray = new String[SIZE];
            nextArray[PROD_NAME] = prod.getProductName();
            returnValue.add(nextArray);
        }
        return returnValue;

    }

    //this is used in the browse and select use case

    public static String[] extractProdInfoForCust(Product product) {
        final int SIZE = 4;
        final int PROD_NAME = 0;
        final int UNIT_PRICE = 1;
        final int QUANTITY = 2;
        final int DESCRIPTION = 3;
        String[] returnValue = new String[SIZE];
        if (product != null) {
            returnValue[PROD_NAME] = product.getProductName();
            returnValue[UNIT_PRICE] = product.getUnitPrice();
            returnValue[QUANTITY] = product.getQuantityAvail();
            returnValue[DESCRIPTION] = product.getDescription();

        }
        return returnValue;

    }

    //this is used in the Product manager use case

    public static String[] extractProdInfoForManager(Product product) {
        final int SIZE = 4;
        final int PROD_NAME = 0;
        final int UNIT_PRICE = 1;
        final int MFG_DATE = 2;
        final int QUANTITY_AVAIL = 3;
        String[] returnValue = new String[SIZE];
        if (product != null) {
            returnValue[PROD_NAME] = product.getProductName();
            returnValue[UNIT_PRICE] = product.getUnitPrice();
            returnValue[MFG_DATE] = product.getMfgDate();
            returnValue[QUANTITY_AVAIL] = product.getQuantityAvail();

        }
        return returnValue;

    }

    public static List<String[]> extractProductInfoForManager(List<Product> list) {
        int size = list.size();
        ArrayList retList = new ArrayList();
        for (Product prod : list) {
            retList.add(extractProdInfoForManager(prod));
        }
        return retList;

    }
}
