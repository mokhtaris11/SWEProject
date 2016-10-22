package business.rulesubsystem;

import business.exceptions.BusinessException;
import business.exceptions.ParseException;
import business.exceptions.RuleException;
import business.externalinterfaces.Rules;
import business.externalinterfaces.RulesConfigKey;
import business.externalinterfaces.RulesConfigProperties;
import business.externalinterfaces.RulesSubsystem;
import java.io.*;
import java.net.URL;
import java.util.List;

import rulesengine.*;


public class RulesSubsystemFacade
        implements RulesSubsystem {

    private final String dirPrefix
            = (new RulesConfigProperties()).getProperty(RulesConfigKey.DIR_PREFIX.getVal());

    @Override
    public void runRules(Rules rulesIface)
            throws BusinessException, RuleException {
        rulesIface.prepareData();
        ReteWrapper wrapper = new ReteWrapper();
        String nameOfRulesFile = rulesIface.getRulesFile();

        wrapper.setTable(rulesIface.getTable());
        wrapper.setCurrentModule(rulesIface.getModuleName());
        try {
            URL url = this.getClass().getClassLoader().
                    getResource(dirPrefix + nameOfRulesFile);
            BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
            wrapper.setRulesAsString(reader);
            wrapper.runRules();
            List<String> updates = wrapper.getUpdates();
            rulesIface.populateEntities(updates);

        } catch (IOException iox) {
            throw new BusinessException(iox.getMessage());
        } catch (OperatingException ox) {
            throw new BusinessException(ox.getMessage());
        } catch (ValidationException vx) {
            throw new RuleException(vx.getMessage());
        }
    }

    String readFile(BufferedReader reader)
            throws ParseException {
        String theString = null;
        String newline = System.getProperty("line.separator");
        try {
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append(newline);
            }
            theString = sb.toString();

        } catch (IOException e) {

            throw new ParseException(e.getMessage());

        }
        return theString;
    }
}
