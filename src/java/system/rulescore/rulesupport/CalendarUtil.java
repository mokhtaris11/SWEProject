package rulesupport;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CalendarUtil {

    private static final Logger LOG = Logger.getLogger(CalendarUtil.class.getName());

    /* accepts a string in the format of month
     * day year, with various possible delimiters
     * we consider -, ., _ as possible delimiters
     * returned format is
     *    mm/dd/yyyy
     */
    public static String standardFormat(String date) {
        date = date.replace('.', '/');
        date = date.replace('-', '/');
        date = date.replace('_', '/');
        String[] tokens = date.split("/");
        if (tokens.length != 3) {
            return date;
        }
        String month = tokens[0];
        String day = tokens[1];
        String year = tokens[2];
        if (month.length() == 1) {
            month = "0" + month;
        }
        if (day.length() == 1) {
            day = "0" + day;
        }
        if (year.length() == 2) {
            year = "20" + year;
        }
        return month + "/" + day + "/" + year;
    }

    static GregorianCalendar makeCalendar(String date)
            throws BadFormatException {
        String dateGoodFormat = standardFormat(date);
        String[] tokens = dateGoodFormat.split("/");
        if (tokens.length != 3) {
            throw new BadFormatException();
        }
        int month = Integer.parseInt(tokens[0]);
        int day = Integer.parseInt(tokens[1]);
        int year = Integer.parseInt(tokens[2]);

        //in GregorianCalendar, month count begins at 0, not 1
        return new GregorianCalendar(year, month - 1, day);

    }

    public static boolean isAfterToday(Object ob) {
        return isAfterToday(ob.toString());
    }

    public static boolean isAfterToday(String date) {
        try {
            LOG.config("Checking expiration date rule...");
            GregorianCalendar cal = makeCalendar(date);
            GregorianCalendar today = new GregorianCalendar();
            boolean result = cal.compareTo(today) > 0;
            LOG.log(Level.CONFIG, "...today: {0}", today.getTime());
            LOG.log(Level.CONFIG, "...passed in date: {0}", cal.getTime());
            LOG.log(Level.CONFIG, "...is it true that passed in date comes after today? {0}", result);
            return result;
        } catch (BadFormatException bfe) {
            return false;
        }
    }

    public static boolean isBeforeToday(String date) {
        try {
            GregorianCalendar cal = makeCalendar(date);
            GregorianCalendar today = new GregorianCalendar();
            return cal.compareTo(today) < 0;
        } catch (BadFormatException bfe) {
            return false;
        }
    }

    static class BadFormatException
            extends java.lang.Exception {

        private static final long serialVersionUID = 1L;

        public BadFormatException() {
            super();
        }

        public BadFormatException(String msg) {
            super(msg);
        }
    }
}
