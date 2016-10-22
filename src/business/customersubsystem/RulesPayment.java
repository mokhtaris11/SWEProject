package business.customersubsystem;

import business.exceptions.BusinessException;
import business.exceptions.RuleException;
import java.util.*;

import business.externalinterfaces.DynamicBean;
import business.externalinterfaces.Address;
import business.externalinterfaces.CreditCard;
import business.externalinterfaces.Rules;
import business.externalinterfaces.RulesConfigKey;
import business.externalinterfaces.RulesConfigProperties;
import business.externalinterfaces.RulesSubsystem;
import business.rulesbeans.PaymentBean;
import business.rulesubsystem.RulesSubsystemFacade;

public class RulesPayment
        implements Rules {

    private HashMap<String, DynamicBean> table;
    private DynamicBean bean;
    private RulesConfigProperties config = new RulesConfigProperties();

    public RulesPayment(Address address, CreditCard creditCard) {
        bean = new PaymentBean(address, creditCard);
    }

    @Override
    public String getModuleName() {
        return config.getProperty(RulesConfigKey.PAYMENT_MODULE.getVal());
    }

    @Override
    public String getRulesFile() {
        return config.getProperty(RulesConfigKey.PAYMENT_RULES_FILE.getVal());
    }

    @Override
    public void prepareData() {
        table = new HashMap<String, DynamicBean>();
        String deftemplate = config.getProperty(RulesConfigKey.PAYMENT_DEFTEMPLATE.getVal());
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

    @Override
    public void populateEntities(List<String> updates) {

    }

    @Override
    public List<CreditCardImpl> getUpdates() {
        return null;
    }

}
