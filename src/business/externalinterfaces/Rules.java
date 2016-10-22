package business.externalinterfaces;

import business.exceptions.BusinessException;
import business.exceptions.RuleException;
import java.util.HashMap;
import java.util.List;

public interface Rules {

    String getModuleName();

    String getRulesFile();

    void prepareData();

    HashMap<String, DynamicBean> getTable();

    void runRules()
            throws BusinessException, RuleException;

    void populateEntities(List<String> updates);

    //updates are placed in a List -- object types may vary
    List getUpdates();

}
