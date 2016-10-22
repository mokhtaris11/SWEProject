package business.logging;

import java.util.logging.LogRecord;
import java.util.logging.SimpleFormatter;

public class EbazSimpleFormatter
        extends SimpleFormatter {

    @Override
    public String format(LogRecord record) {
        return record.getLevel().toString() + " For Yellow Group: " + record.getMessage() + "\r\n";
    }
}
