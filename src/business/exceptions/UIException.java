package business.exceptions;

public class UIException
        extends BusinessException {

    public UIException(String msg) {
        super(msg);

    }

    public UIException(Exception e) {
        super(e);
    }
    private static final long serialVersionUID = 3689355398893482807L;

}
