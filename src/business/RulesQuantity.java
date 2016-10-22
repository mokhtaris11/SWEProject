package business;

import business.exceptions.BusinessException;
import business.externalinterfaces.DynamicBean;
import business.externalinterfaces.Rules;
import business.externalinterfaces.RulesConfigKey;
import business.externalinterfaces.RulesConfigProperties;
import business.externalinterfaces.RulesSubsystem;
import business.rulesbeans.QuantityBean;
import business.rulesubsystem.RulesSubsystemFacade;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class RulesQuantity
        implements Rules {

    private HashMap<String, DynamicBean> table;
    private DynamicBean bean;
    private RulesConfigProperties config = new RulesConfigProperties();

    public RulesQuantity(Quantity quantity) {
        bean = new QuantityBean(quantity);
    }

    @Override
    public String getModuleName() {
        return config.getProperty(RulesConfigKey.QUANTITY_MODULE.getVal());
    }

    @Override
    public String getRulesFile() {
        return config.getProperty(RulesConfigKey.QUANTITY_RULES_FILE.getVal());
    }

    @Override
    public void prepareData() {
        table = new HashMap<String, DynamicBean>();
        String deftemplate = config.getProperty(RulesConfigKey.QUANTITY_DEFTEMPLATE.getVal());
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

    @Override
    public List<Object> getUpdates() {
        // nothing to do
        return new ArrayList<Object>();
    }

    @Override
    public void populateEntities(List<String> updates) {
        // nothing to do

    }

}
