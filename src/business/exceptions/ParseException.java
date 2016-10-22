/*
 *
 */
package business.exceptions;

/**
 *
 */
public class ParseException
        extends Exception {

    public ParseException(String msg) {
        super(msg);
    }

    public ParseException(Exception e) {
        super(e);
    }
    private static final long serialVersionUID = 3258144448058015026L;
}
