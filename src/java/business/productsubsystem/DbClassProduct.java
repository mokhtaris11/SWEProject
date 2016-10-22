package business.productsubsystem;

import business.*;
import business.exceptions.BackendException;
import business.externalinterfaces.DbClassProductForTest;
import business.externalinterfaces.Product;
import business.externalinterfaces.ProductFromGui;
import static business.util.StringParse.*;
import business.util.TwoKeyHashMap;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import middleware.DbConfigProperties;
import middleware.dataaccess.DataAccessSubsystemFacade;
import middleware.exceptions.DatabaseException;
import middleware.externalinterfaces.DataAccessSubsystem;
import middleware.externalinterfaces.DbClass;
import middleware.externalinterfaces.DbConfigKey;

class DbClassProduct
        implements DbClass, DbClassProductForTest {

    private static final Logger LOG = Logger.getLogger(DbClassProduct.class
            .getPackage().getName());
    private DataAccessSubsystem dataAccessSS = new DataAccessSubsystemFacade();

    /**
     * The productTable matches product ID with Product object. It is static so that requests for "read
     * product" based on product ID can be handled without extra db hits
     */
    private static TwoKeyHashMap<Integer, String, Product> productTable;
    private String queryType;
    private String query;
    private Product product;
    private ProductFromGui prodFromGui;
    private String description;
    private List<Product> productList;
    Integer catalogId;
    Integer productId;

    private final String LOAD_PROD_TABLE = "LoadProdTable";
    private final String READ_PRODUCT = "ReadProduct";
    private final String READ_PROD_LIST = "ReadProdList";
    private final String SAVE_NEW_PROD = "SaveNewProd";
    private final String DELETE_PROD = "DeleteProduct";
    private final String SAVE_EDIT_PROD = "SaveEditProduct";

    @Override
    public void buildQuery() {
        if (queryType.equals(LOAD_PROD_TABLE)) {
            buildProdTableQuery();
        } else if (queryType.equals(READ_PRODUCT)) {
            buildReadProductQuery();
        } else if (queryType.equals(READ_PROD_LIST)) {
            buildProdListQuery();
        } else if (queryType.equals(SAVE_NEW_PROD)) {
            buildSaveNewQuery();
        } else if (queryType.equals(DELETE_PROD)) {
            buildDeleteProductQuery();
        } else if (queryType.equals(SAVE_EDIT_PROD)) {
            buildSaveEditProductQuery();
        }

    }

    private void buildDeleteProductQuery() {
        query = "DELETE FROM Product WHERE ProductId=" + productId;
    }

    private void buildProdTableQuery() {
        query = "SELECT * FROM product";
    }

    private void buildProdListQuery() {
        query = "SELECT * FROM Product WHERE catalogid = " + catalogId;
    }

    private void buildReadProductQuery() {
        query = "SELECT * FROM Product WHERE productid = " + productId;
    }

    private void buildSaveEditProductQuery() {
        query = "UPDATE Product Set productname='" + prodFromGui.getProductName()
                + "',totalquantity=" + prodFromGui.getQuantityAvail()
                + ",priceperunit=" + prodFromGui.getUnitPrice()
                + ",mfgdate='" + prodFromGui.getMfgDate() + "'"
                + ",catalogid=" + catalogId
                + " "
                + " WHERE productid=" + productId;
    }

    private void buildSaveNewQuery() {
        query = "INSERT into Product "
                + "(productid,productname,totalquantity,priceperunit,mfgdate,catalogid,description) "
                + "VALUES(NULL,'" + prodFromGui.getProductName() + "',"
                + prodFromGui.getQuantityAvail() + ","
                + prodFromGui.getUnitPrice() + ",'" + prodFromGui.getMfgDate()
                + "'," + catalogId + ",'" + description + "')";
    }

    public TwoKeyHashMap<Integer, String, Product> readProductTable()
            throws BackendException {
        if (productTable != null) {
            return productTable.clone();
        }
        return refreshProductTable();
    }

    /**
     * Force a database call
     */
    @Override
    public TwoKeyHashMap<Integer, String, Product> refreshProductTable()
            throws BackendException {
        queryType = LOAD_PROD_TABLE;
        try {
            dataAccessSS.atomicRead(this);
        } catch (DatabaseException e) {
            throw new BackendException(e);
        }

        // Return a clone since productTable must not be corrupted
        return productTable.clone();
    }

    public List<Product> readProductList(Integer catalogId)
            throws BackendException {
        if (productList == null) {
            return refreshProductList(catalogId);
        }
        return Collections.unmodifiableList(productList);
    }

    public void deleteProduct(int productId)
            throws BackendException {
        this.productId = productId;
        queryType = DELETE_PROD;
        try {
            dataAccessSS.deleteWithinTransaction(this);
        } catch (DatabaseException e) {
            LOG.log(Level.WARNING, "A DatabaseException was thrown: {0}", e.getMessage());
            throw new BackendException(e);
        }
    }

    private List<Product> refreshProductList(Integer catalogId)
            throws BackendException {
        this.catalogId = catalogId;
        queryType = READ_PROD_LIST;
        try {
            dataAccessSS.atomicRead(this);
        } catch (DatabaseException e) {
            LOG.log(Level.WARNING, "A DatabaseException was thrown: {0}", e.getMessage());
            throw new BackendException(e);
        }
        return productList;
    }

    public Product readProduct(Integer productId)
            throws BackendException {
        this.productId = productId;
        queryType = READ_PRODUCT;
        try {
            dataAccessSS.atomicRead(this);
        } catch (DatabaseException e) {
            LOG.log(Level.WARNING, "A DatabaseException was thrown: {0}", e.getMessage());
            throw new BackendException(e);
        }
        return product;
    }

    /**
     * Database columns: productid, productname, totalquantity, priceperunit, mfgdate, catalogid, description
     */
    public void saveNewProduct(ProductFromGui product, Integer catalogid,
            String description)
            throws BackendException {
        this.prodFromGui = product;
        this.catalogId = catalogid;
        this.description = description;
        queryType = this.SAVE_NEW_PROD;
        try {
            dataAccessSS.saveWithinTransaction(this);
        } catch (DatabaseException e) {
            LOG.log(Level.WARNING, "A DatabaseException was thrown: {0}", e.getMessage());
            throw new BackendException(e);
        }
    }

    public void saveEditProduct(ProductFromGui product, Integer catalogid, Integer productId)
            throws BackendException {
        this.prodFromGui = product;
        this.catalogId = catalogid;
        this.productId = productId;
        queryType = this.SAVE_EDIT_PROD;
        try {
            dataAccessSS.saveWithinTransaction(this);
        } catch (DatabaseException e) {
            LOG.log(Level.WARNING, "A DatabaseException was thrown: {0}", e.getMessage());
            throw new BackendException(e);
        }
    }

    @Override
    public void populateEntity(ResultSet resultSet)
            throws DatabaseException {
        if (queryType.equals(LOAD_PROD_TABLE)) {
            populateProdTable(resultSet);
        } else if (queryType.equals(READ_PRODUCT)) {
            populateProduct(resultSet);
        } else if (queryType.equals(READ_PROD_LIST)) {
            populateProdList(resultSet);
        }
    }

    private void populateProdList(ResultSet rs)
            throws DatabaseException {
        productList = new LinkedList<Product>();
        try {
            Product product = null;
            Integer prodId = null;
            String productName = null;
            String quantityAvail = null;
            String unitPrice = null;
            String mfgDate = null;
            Integer catalogId = null;
            String description = null;
            while (rs.next()) {
                prodId = rs.getInt("productid");
                productName = rs.getString("productname");
                quantityAvail = makeString(rs.getInt("totalquantity"));
                unitPrice = makeString(rs.getDouble("priceperunit"));
                mfgDate = rs.getString("mfgdate");
                catalogId = rs.getInt("catalogid");
                description = rs.getString("description");
                product = new ProductImpl(prodId, productName, quantityAvail,
                        unitPrice, mfgDate, catalogId, description);
                productList.add(product);
            }
        } catch (SQLException e) {
            LOG.log(Level.WARNING, "A SQLException was thrown: {0}", e.getMessage());
            throw new DatabaseException(e);
        }
    }

    /**
     * Internal method to ensure that product table is up to date.
     */
    private void populateProdTable(ResultSet rs)
            throws DatabaseException {
        productTable = new TwoKeyHashMap<Integer, String, Product>();
        try {
            Product product = null;
            Integer prodId = null;
            String productName = null;
            String quantityAvail = null;
            String unitPrice = null;
            String mfgDate = null;
            Integer catalogId = null;
            String description = null;
            while (rs.next()) {
                prodId = rs.getInt("productid");
                productName = rs.getString("productname");
                quantityAvail = makeString(rs.getInt("totalquantity"));
                unitPrice = makeString(rs.getDouble("priceperunit"));
                mfgDate = rs.getString("mfgdate");
                catalogId = rs.getInt("catalogid");
                description = rs.getString("description");
                product = new ProductImpl(prodId, productName, quantityAvail,
                        unitPrice, mfgDate, catalogId, description);
                productTable.put(prodId, productName, product);
            }
        } catch (SQLException e) {
            LOG.log(Level.WARNING, "A SQLException was thrown: {0}", e.getMessage());
            throw new DatabaseException(e);
        }
    }

    private void populateProduct(ResultSet rs)
            throws DatabaseException {
        try {
            Integer prodId = null;
            String productName = null;
            String quantityAvail = null;
            String unitPrice = null;
            String mfgDate = null;
            Integer catalogId = null;
            String description = null;
            if (rs.next()) {
                prodId = rs.getInt("productid");
                productName = rs.getString("productname");
                quantityAvail = makeString(rs.getInt("totalquantity"));
                unitPrice = makeString(rs.getDouble("priceperunit"));
                mfgDate = rs.getString("mfgdate");
                catalogId = rs.getInt("catalogid");
                description = rs.getString("description");
                product = new ProductImpl(prodId, productName, quantityAvail,
                        unitPrice, mfgDate, catalogId, description);
            }
        } catch (SQLException e) {
            LOG.log(Level.WARNING, "A SQLException was thrown: {0}", e.getMessage());
            throw new DatabaseException(e);
        }
    }

    @Override
    public String getDbUrl() {
        DbConfigProperties props = new DbConfigProperties();
        return props.getProperty(DbConfigKey.PRODUCT_DB_URL.getVal());
    }

    @Override
    public String getQuery() {
        return query;
    }

}
