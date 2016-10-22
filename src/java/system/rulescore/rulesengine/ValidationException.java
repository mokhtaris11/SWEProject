package rulesengine;

public class ValidationException
        extends Exception {

    private static final long serialVersionUID = 1096831386474534653L;

    public ValidationException() {
        super();
    }

    public ValidationException(String msg) {
        super(msg);
    }
}
