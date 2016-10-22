package business.shoppingcartsubsystem;

import business.RuleException;
import business.exceptions.BusinessException;
import business.externalinterfaces.DynamicBean;
import business.externalinterfaces.ShoppingCart;
import business.externalinterfaces.Rules;
import business.externalinterfaces.RulesConfigKey;
import business.externalinterfaces.RulesConfigProperties;
import business.externalinterfaces.RulesSubsystem;
import business.rulesbeans.FinalOrderBean;
import business.rulesubsystem.RulesSubsystemFacade;
import java.util.HashMap;
import java.util.List;

/**
 * This Rules class is different from RulesShoppingCart (even though it checks aspects of a Shopping Cart)
 * because it needs to execute at a different time during execution of the application (namely, when final
 * order is submitted)
 */
public class RulesFinalOrder
        implements Rules {

    public RulesFinalOrder(ShoppingCart shoppingCart) {
        bean = new FinalOrderBean(shoppingCart);

        //don't call prepare data here -- we don't know 
        //what else might need to be done first
        //let the RulesSubsystem make that call
    }

    ///////////////implementation of interface
    @Override
    public String getModuleName() {
        return config.getProperty(RulesConfigKey.FINAL_ORDER_MODULE.getVal());
    }

    @Override
    public String getRulesFile() {
        return config.getProperty(RulesConfigKey.FINAL_ORDER_RULES_FILE.getVal());
    }

    @Override
    public void prepareData() {
        table = new HashMap<String, DynamicBean>();
        String deftemplate = config.getProperty(RulesConfigKey.FINAL_ORDER_DEFTEMPLATE.getVal());
        table.put(deftemplate, bean);
    }

    @Override
    public void runRules()
            throws BusinessException, RuleException {
        RulesSubsystem rules = new RulesSubsystemFacade();
        rules.runRules(this);
    }

    @Override
    public HashMap<String, DynamicBean> getTable() {
        return table;
    }
    /* expect a list of address values, in order
     * street, city, state ,zip
     */

    @Override
    public void populateEntities(List<String> updates) {
        //do nothing
    }

    @Override
    public List getUpdates() {
        //do nothing
        return null;
    }

    private HashMap<String, DynamicBean> table;
    private DynamicBean bean;
    private RulesConfigProperties config = new RulesConfigProperties();

}
