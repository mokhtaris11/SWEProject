package business.productsubsystem;

import business.externalinterfaces.Catalog;

public class CatalogImpl
        implements Catalog {

    private String catId;
    private String name;

    public CatalogImpl(String id, String name) {
        this.catId = id;
        this.name = name;
    }

    @Override
    public String getId() {
        return catId;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setId(String id) {
        catId = id;

    }

    @Override
    public void setName(String name) {
        this.name = name;

    }

}
