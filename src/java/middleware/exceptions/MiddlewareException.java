package middleware.exceptions;

public class MiddlewareException
        extends Exception {

    public MiddlewareException() {
        super();
    }

    public MiddlewareException(String msg) {
        super(msg);
    }

    public MiddlewareException(Exception e) {
        super(e);
    }
}
