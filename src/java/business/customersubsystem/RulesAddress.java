package business.customersubsystem;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;

import business.exceptions.BusinessException;
import business.exceptions.RuleException;
import business.externalinterfaces.DynamicBean;
import business.externalinterfaces.Address;
import business.externalinterfaces.Rules;
import business.externalinterfaces.RulesSubsystem;
import business.externalinterfaces.RulesConfigKey;
import business.externalinterfaces.RulesConfigProperties;
import business.rulesbeans.AddressBean;
import business.rulesubsystem.RulesSubsystemFacade;

class RulesAddress
        implements Rules {

    private HashMap<String, DynamicBean> table;
    private DynamicBean bean;
    private AddressImpl updatedAddress;
    private RulesConfigProperties config = new RulesConfigProperties();

    RulesAddress(Address address) {
        bean = new AddressBean(address);
    }

    ///////////////implementation of interface
    @Override
    public String getModuleName() {
        return config.getProperty(RulesConfigKey.ADDRESS_MODULE.getVal());
    }

    @Override
    public String getRulesFile() {
        return config.getProperty(RulesConfigKey.ADDRESS_RULES_FILE.getVal());
    }

    @Override
    public void prepareData() {
        table = new HashMap<String, DynamicBean>();
        String deftemplate = config.getProperty(RulesConfigKey.ADDRESS_DEFTEMPLATE.getVal());
        table.put(deftemplate, bean);

    }

    @Override
    public HashMap<String, DynamicBean> getTable() {
        return table;
    }

    @Override
    public void runRules()
            throws BusinessException, RuleException {
        RulesSubsystem rules = new RulesSubsystemFacade();
        rules.runRules(this);
    }
    /* expect a list of address values, in order
     * street, city, state ,zip
     */

    @Override
    public void populateEntities(List<String> updates) {
        updatedAddress = new AddressImpl(updates.get(0),
                updates.get(1),
                updates.get(2),
                updates.get(3));

    }

    @Override
    public List<AddressImpl> getUpdates() {
        List<AddressImpl> retVal = new ArrayList<AddressImpl>();
        retVal.add(updatedAddress);
        return retVal;
    }

}
