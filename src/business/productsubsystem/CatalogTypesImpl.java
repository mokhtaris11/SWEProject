package business.productsubsystem;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import business.externalinterfaces.CatalogTypes;

/**
 *  pcorazza
 * <p>
 * Class Description: This is a bean...it just holds data in memory. It is not the entity class for a catalog
 * -- it is just reference data, stored in memory.
 */
public class CatalogTypesImpl
        implements CatalogTypes {

    HashMap<Integer, String> catalogIdToName = new HashMap<Integer, String>();
    HashMap<String, Integer> catalogNameToId = new HashMap<String, Integer>();

    @Override
    public List<String[]> getCatalogNamesStringArrays() {
        List<String[]> retVal = new ArrayList<String[]>();
        Collection<String> vals = catalogIdToName.values();
        for (String s : vals) {
            retVal.add(new String[]{s});
        }
        return retVal;

    }

    @Override
    public List<String> getCatalogNames() {
        String[] names = catalogIdToName.values().toArray(new String[0]);
        return Arrays.asList(names);
    }

    @Override
    public String getCatalogName(Integer id) {
        return catalogIdToName.get(id);
    }

    @Override
    public void addCatalog(Integer id, String name) {
        catalogIdToName.put(id, name);
        catalogNameToId.put(name, id);
    }

    @Override
    public Integer getCatalogId(String name) {
        return catalogNameToId.get(name);

    }

}
