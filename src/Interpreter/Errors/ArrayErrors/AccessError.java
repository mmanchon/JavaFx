package Interpreter.Errors.ArrayErrors;

import Interpreter.Errors.BasicError;
import javafx.scene.image.Image;

import java.net.URI;

public class AccessError extends BasicError {

    private Object tryingAccess;
    private Object minimumLimitAccess;
    private Object maximumLimitAccess;

    public AccessError(String message, Image image, Object tryingAccess, Object minimumLimitAccess, Object maximumLimitAccess, URI url) {
        super(message, image, url);
        this.tryingAccess = tryingAccess;
        this.minimumLimitAccess = minimumLimitAccess;
        this.maximumLimitAccess = maximumLimitAccess;
    }

    public Object getTryingAccess() {
        return tryingAccess;
    }

    public void setTryingAccess(Object tryingAccess) {
        this.tryingAccess = tryingAccess;
    }

    public Object getMinimumLimitAccess() {
        return minimumLimitAccess;
    }

    public void setMinimumLimitAccess(Object minimumLimitAccess) {
        this.minimumLimitAccess = minimumLimitAccess;
    }

    public Object getMaximumLimitAccess() {
        return maximumLimitAccess;
    }

    public void setMaximumLimitAccess(Object maximumLimitAccess) {
        this.maximumLimitAccess = maximumLimitAccess;
    }
}
