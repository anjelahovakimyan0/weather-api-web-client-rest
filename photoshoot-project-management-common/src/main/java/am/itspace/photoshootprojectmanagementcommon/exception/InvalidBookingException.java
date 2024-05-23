package am.itspace.photoshootprojectmanagementcommon.exception;

public class InvalidBookingException extends RuntimeException {

    public InvalidBookingException() {
        super();
    }

    public InvalidBookingException(String message) {
        super(message);
    }

    public InvalidBookingException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidBookingException(Throwable cause) {
        super(cause);
    }

    protected InvalidBookingException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
