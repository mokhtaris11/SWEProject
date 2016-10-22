package rulesengine;

import jess.*;

import java.io.*;
import java.io.IOException;
import java.util.*;


/*
 * For efficiency, we maintain a single instance
 * of the Rete engine in memory. However, it is convenient
 * to provide a local environment for each user of Rete.
 * 
 * Users of rules create their own instance of the ReteWrapper
 * but share the Rete engine.
 * 
 * Access to Rete is serialized via the synchronized method
 * runRules
 */
public class ReteWrapper {

    //maintain a static intstance

    private static Rete engine = new Rete();

    /* All rules will belong to a single file */
    private String rulesAsString;
    /* matches name of template to the instance of Rules class */
    private HashMap table;
    private final String UPDATE_KEY = "update";
    private String currentModule;

    private List<String> updates;

    public ReteWrapper() {
		//instead, we maintain a static instance
        //engine = new Rete();
    }

	////public interface
    ////  require the following:
    ////  1. the rules (as a string of Jess code)
    ////  2. the hashtable that matches a deftemplate with a bean
    ////  3. the module (if any)
    ////  having provided these, a client may then
    ////  4. runRules (and catch exceptions)
    ////  5. getUpdates
    /**
     * Assumes not encoded
     */
    public void setRulesAsString(BufferedReader rules)
            throws IOException {
        rulesAsString = readFile(rules);
    }

    public void setRulesAsString(File rules, boolean isEncoded)
            throws IOException {
        if (isEncoded) {
            rulesAsString = util.CodingUtils.decodeAsString(rules);
        } else {
            rulesAsString = readFile(convertToReader(rules));
        }
        //LOG.info(rulesAsString);
    }

    BufferedReader convertToReader(File f)
            throws IOException {
        return new BufferedReader(new FileReader(f));
    }

    String readFile(BufferedReader reader)
            throws IOException {
        String theString = null;
        String newline = System.getProperty("line.separator");
        StringBuilder sb = new StringBuilder();
        String line = null;
        while ((line = reader.readLine()) != null) {
            sb.append(line).append(newline);
        }
        theString = sb.toString();

        return theString;
    }

    public void setTable(HashMap table) {
        this.table = table;
    }

    public void setCurrentModule(String currModule) {
        currentModule = currModule;
    }

	//use of engine must be serialized--therefore the method
    //is synchronized
    //to scale, manage a pool of Rete instances
    public synchronized void runRules()
            throws OperatingException, ValidationException {
        clearEngine();
        loadJessFiles();
        prepareData();
        execute();

    }

    public List<String> getUpdates() {
        return updates;
    }

	/////// private support methods
	//convenience method to clear the rete engine --
    //allows me to convert a possible Jess exception to one
    //of my excpetion types
    private void clearEngine()
            throws OperatingException {
        try {
            engine.clear();
        } catch (JessException e) {
            throw new OperatingException(e.getMessage());
        }

    }

	//load the rules into memory
    //the jess file will include a defmodule command 
    //to ensure the rules are placed in the right module
    private void loadJessFiles()
            throws OperatingException {
        try {
            engine.eval(rulesAsString);
            //LOG.info(rulesAsString);
        } catch (JessException e) {
            throw new OperatingException(e.getMessage());
        }
    }

    //read the hashtable -- create corresponding definstances

    private void prepareData()
            throws OperatingException {
        try {
            Iterator it = table.keySet().iterator();
			//we leave open the possibility that more than one
            //java bean may be needed
            while (it.hasNext()) {
                String key = (String) it.next();
                Object ob = table.get(key);
                engine.definstance(key, ob, true);
            }

        } catch (JessException e) {
            e.printStackTrace();
            throw new OperatingException(e.getMessage());
        }

    }
	//this resets, sets focus to correct module, and runs; then
    //sets update values

    private void execute()
            throws OperatingException, ValidationException {
        try {
            engine.reset();
            if (currentModule != null) {
                engine.setFocus(currentModule);
            }
            engine.run();
            setUpdateValues(engine.fetch(UPDATE_KEY));
        } catch (JessException e) {
            Throwable x = null;
            if ((x = e.getCause()) instanceof ValidationException) {
                //LOG.info(x.getClass().getName());
                throw new ValidationException(x.getMessage());
            } else {
				//LOG.info("Getting an exception of type "+e.getClass().getName());
                //LOG.info("which contains an exception of type "+e.getCause().getClass().getName());
                throw new OperatingException(e.getCause().getMessage());
            }
        }

    }
    /* We have set it up so that we return a List of strings and
     * leave conversions to the calling clases
     */

    private void setUpdateValues(Value v)
            throws OperatingException {
        try {
            if (v != null) {
                updates = new ArrayList<String>();

                ValueVector vv = v.listValue(engine.getGlobalContext());
                int size = vv.size();
                for (int i = 0; i < size; ++i) {
                    updates.add(vv.get(i).stringValue(engine.getGlobalContext()));
                }
            }
        } catch (JessException e) {
            //LOG.info("in setUpdateValues, error: "+e.getClass().getName());
            throw new OperatingException(e.getMessage());
        }

    }
}
