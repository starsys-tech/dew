package group.idealworld.dew.core.hi.exception;

/**
 * The type Rt service not found exception.
 *
 * @author gudaoxuri
 */
public class RTAPINotFoundException extends RuntimeException {

    /**
     * Instantiates a new Rt exception.
     */
    public RTAPINotFoundException() {
    }

    /**
     * Instantiates a new Rt exception.
     *
     * @param message the message
     */
    public RTAPINotFoundException(String message) {
        super(message);
    }

    /**
     * Instantiates a new Rt exception.
     *
     * @param message the message
     * @param cause   the cause
     */
    public RTAPINotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Instantiates a new Rt exception.
     *
     * @param cause the cause
     */
    public RTAPINotFoundException(Throwable cause) {
        super(cause);
    }

    /**
     * Instantiates a new Rt exception.
     *
     * @param message            the message
     * @param cause              the cause
     * @param enableSuppression  the enable suppression
     * @param writableStackTrace the writable stack trace
     */
    public RTAPINotFoundException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
