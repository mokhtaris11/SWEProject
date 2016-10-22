package middleware.dataaccess;

import static business.util.StringParse.isEmptyString;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;
import java.util.logging.Level;
import java.util.logging.Logger;
import middleware.DbConfigProperties;
import middleware.exceptions.DatabaseException;
import middleware.externalinterfaces.Cleanup;
import middleware.externalinterfaces.DbConfigKey;

//remove this import statement for jdk 1.4
//import java.util.Queue;
/**
 * Here is the typical way to use this class; the query variable stores the necessary SQL statement:
 * SimpleConnectionPool pool = SimpleConnectionPool.getInstance(DataAccessSubsystemFacade.MAX_CONNECTIONS);
 * Connection con = pool.getConnection(dbUrl); ResultSet rs = SimpleConnectionPool.doQuery(con,query);
 * pool.returnToPool(con,dbUrl);
 */
class SimpleConnectionPool {

    private SimpleConnectionPool(String dbuser, String dbpass, String drivername, int maxconn)
            throws DatabaseException {
        this.freeConnectionsMap = new HashMap<String, Queue<Connection>>();
        this.dbuser = isEmptyString(dbuser) ? DEFAULT_USER : dbuser;
        this.dbpass = dbpass;
        this.drivername = isEmptyString(drivername) ? DEFAULT_DRIVER
                : drivername;
        this.maxconn = isEmptyString(maxconn) ? Integer
                .parseInt(DEFAULT_MAX_CONN) : maxconn;
        loadJDBCDriver();
    }

    private Queue<Connection> freeConnections(String URI) {
        return freeConnectionsMap.get(URI);
    }

    private int numFreeConnections(String URI) {
        Queue<Connection> cons = freeConnections(URI);
        if (cons == null) {
            return 0;
        }
        return cons.size();
    }

    private boolean noClientsSoFar(String URI) {
        Integer numCl = numClientsMap.get(URI);
        return (numCl == null || numCl == 0);
    }

    private void initializePool(String URI)
            throws DatabaseException {
        for (int i = 0; i < maxconn; ++i) {
            createConnection(URI);

        }
    }

    private void loadJDBCDriver()
            throws DatabaseException {
        try {
            Class.forName(drivername);
        } catch (java.lang.ClassNotFoundException e) {
            LOG.log(Level.WARNING, "ClassNotFoundException: {0}", e.getMessage());
            throw new DatabaseException(e);
        }
    }

    private void addConnection(String URI, Connection con) {
        Queue<Connection> freeForURI = freeConnectionsMap.get(URI);
        if (freeForURI == null) {
            freeForURI = new LinkedList<Connection>();
        }

        freeForURI.add(con);
        freeConnectionsMap.put(URI, freeForURI);
    }

    private Connection createConnection(String URI)
            throws DatabaseException {
        Connection con = null;
        try {
            if (this.dbuser == null || dbuser.equals("")) {
                con = DriverManager.getConnection(URI);
            } else {
                con = DriverManager
                        .getConnection(URI, this.dbuser, this.dbpass);
            }
            // add connection to pool
            LOG.info("Adding new connection to pool");
            addConnection(URI, con);

        } catch (SQLException e) {
            LOG.log(Level.WARNING, "Unable to create a connection to database with dburl {0}", URI);
            throw new DatabaseException("Database is unavailable.");
        }
        return con;
    }

    private synchronized void releaseConnectionsInQueue(Queue<Connection> freeConnections) {
        if (freeConnections == null) {
            return;
        }
        for (Connection con : freeConnections) {
            try {
                con.close();
            } catch (SQLException e) {
                LOG.warning("Cannot close connection! (Probably already closed?)");
            }
        }
        freeConnections.clear();
    }

    synchronized Connection getConnection(String URI)
            throws DatabaseException {
        if (noClientsSoFar(URI)) {
            initializePool(URI);
        }
        Connection con = null;
        Queue<Connection> freeConnections = freeConnections(URI);
        if (freeConnections != null && !freeConnections.isEmpty()) {
            LOG.info("Returning a live connection from the pool");
            con = freeConnections.poll();
            try {
                if (con.isClosed()) {
                    LOG.info("Removed closed connection!");
                    // Try again recursively
                    con = getConnection(URI);
                }
            } catch (SQLException e) {
                LOG.info("Removed closed connection!");
                // Try again recursively
                con = getConnection(URI);
            } catch (Exception e) {
                LOG.info("Removed closed connection!");
                // Try again recursively
                con = getConnection(URI);
            }
        } else {
            con = createConnection(URI);
        }
        incrementNumClients(URI);
        return con;
    }

    synchronized void returnToPool(Connection con, String URI) {
        try {
            if ((con != null) && (!con.isClosed())
                    && (numFreeConnections(URI) < this.maxconn)) {
                LOG.info("Returning a connection to pool");
                con.setAutoCommit(true);
                addConnection(URI, con);
            }
        } catch (Exception e) {
            LOG.log(Level.WARNING, "Unable to return connection to pool. Proceeding...{0}", e.getMessage());
            // do nothing -- just don't return con to the pool
        }
    }

    synchronized void closeConnections() {
        LOG.info("Closing all connections");
        Collection<Queue<Connection>> conLists = freeConnectionsMap.values();
        for (Queue<Connection> list : conLists) {
            releaseConnectionsInQueue(list);
        }
    }

    public static void main(String[] args) {
        // insert your own db url and test query
        String testQuery = "SELECT * FROM product";// "select productname from product";
        testConnection("jdbc:odbc:EbazProducts", testQuery, 8);
    }

    private static void incrementNumClients(String URI) {
        Integer num = numClientsMap.get(URI);
        if (num == null) {
            numClientsMap.put(URI, new Integer(1));

        } else {
            Integer next = new Integer(num + 1);
            numClientsMap.put(URI, next);
        }
    }

    private static void testError() {
        LOG.info("Please provide a db url and a test query for the testConnection method "
                + "and a value for maxconn > 0");
    }

    /**
     * This version is for use by Cleanup only -- instances should be gotten by passing in correct URI
     *
     * @return SimpleConnectionPool
     */
    static synchronized SimpleConnectionPool getInstance(Cleanup c) {
        if (instance == null) {
            LOG.info("No instance of SimpleConnectionPool has been created..."
                    + "returning null");
            return null;
        } else {
            return instance;
        }
    }

    /*
     * the default behavior for getInstance: no username or pwd, and use the
     * default driver
     */
    static synchronized SimpleConnectionPool getInstance(int maxconn)
            throws DatabaseException {
        return getInstance("", "", "", maxconn);
    }

    static synchronized SimpleConnectionPool getInstance(String dbuser, String dbpass, String drivername, int maxconn)
            throws DatabaseException {
        if (instance == null) {
            instance = new SimpleConnectionPool(dbuser, dbpass, drivername,
                    maxconn);
        }

        return instance;
    }

    static ResultSet doQuery(Connection con, String query)
            throws DatabaseException {
        if (con == null) {
            LOG.warning("Connection is null -- cannot perform query");
            return null;
        }
        ResultSet rs = null;
        Statement stmt;
        try {
            stmt = con.createStatement();
            rs = stmt.executeQuery(query);

        } catch (SQLException ex) {
            LOG.log(Level.WARNING, "SQLQueryException: {0}", ex.getMessage());
            throw new DatabaseException(ex);
        }

        return rs;
    }

    /**
     * @param con - the connection object
     * @param query - the SQL query
     * @return - number of rows affected if update or delete; generated key if insert with auto-increment key
     * field.
     * @throws DatabaseException
     */
    static Integer doUpdate(Connection con, String query)
            throws DatabaseException {
        if (con == null) {
            LOG.warning("Connection is null -- cannot perform update");
        }

        Statement stmt;
        Integer generatedKey = null;
        try {
            stmt = con.createStatement();
            LOG.log(Level.INFO, "query: {0}", query);
            int numRows = stmt.executeUpdate(query, Statement.RETURN_GENERATED_KEYS);
            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                generatedKey = rs.getInt(1);
                LOG.log(Level.INFO, "Generated key for auto_increment id column after insert: {0}",
                        generatedKey);

            } else {
                LOG.log(Level.INFO, "No generated key for {0}", query);
            }

            return (generatedKey == null) ? numRows : generatedKey;

        } catch (SQLException ex) {
            LOG.log(Level.WARNING, "SQLQueryException: {0}", ex.getMessage());
            throw new DatabaseException(ex);
        }
    }

    static void testConnection(String url, String query, int maxconn) {
        if (url == null || url.length() == 0 || query == null || query.length() == 0 || maxconn <= 0) {
            testError();
        }

        SimpleConnectionPool pool = null;
        Connection con = null;
        try {
            pool = SimpleConnectionPool.getInstance(maxconn);
            LOG.info("creating initial connections...");
            con = pool.getConnection(url);
            LOG.info("got a connection");

            ResultSet rs = SimpleConnectionPool.doQuery(con, query);
            LOG.info("statement created...");
            LOG.info("executing query...");

            int count = 0;

            while (rs.next()) {
                ++count;
                LOG.log(Level.INFO, "row {0}: {1}", new Object[]{count, rs.getString("productname")});
            }

        } catch (Exception e) {
            LOG.log(Level.WARNING, "Error occurred trying to read table: {0} Message: {1}",
                    new Object[]{e.getClass().getName(), e.getMessage()});

        } finally {
            if (pool != null) {
                pool.returnToPool(con, url);
                pool.closeConnections();
            }
        }
    }
    private final HashMap<String, Queue<Connection>> freeConnectionsMap;

    private final String DEFAULT_DRIVER = props.getProperty(DbConfigKey.DRIVER.getVal());
    private final String DEFAULT_USER = props.getProperty(DbConfigKey.DB_USER.getVal());
    private final String DEFAULT_MAX_CONN = props.getProperty(DbConfigKey.MAX_CONNECTIONS.getVal());
    private final String dbuser;
    private final String dbpass;
    private final String drivername;
    private final int maxconn;
    // maps URI -> numclients (Integer)
    private static final HashMap<String, Integer> numClientsMap = new HashMap<String, Integer>();
    private static final Logger LOG = Logger.getLogger(SimpleConnectionPool.class.getName());
    private static SimpleConnectionPool instance;
    static DbConfigProperties props = new DbConfigProperties();

}