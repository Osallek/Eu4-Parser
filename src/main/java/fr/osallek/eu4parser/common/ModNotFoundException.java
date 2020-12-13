package fr.osallek.eu4parser.common;

public class ModNotFoundException extends RuntimeException {

    public ModNotFoundException() {
    }

    public ModNotFoundException(String message) {
        super(message);
    }

    public ModNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public ModNotFoundException(Throwable cause) {
        super(cause);
    }

    public ModNotFoundException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
