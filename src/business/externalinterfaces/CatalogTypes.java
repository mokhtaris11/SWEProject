package business.externalinterfaces;

import java.util.List;

public interface CatalogTypes {

    @SuppressWarnings("unchecked")
    public List<String[]> getCatalogNamesStringArrays();

    public String getCatalogName(Integer id);

    public Integer getCatalogId(String name);

    public void addCatalog(Integer id, String name);

    public List<String> getCatalogNames();
}
