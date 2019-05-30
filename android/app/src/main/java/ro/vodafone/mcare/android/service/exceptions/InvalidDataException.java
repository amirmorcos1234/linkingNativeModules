package ro.vodafone.mcare.android.service.exceptions;

/**
 * Created by Victor Radulescu on 7/25/2017.
 */

public class InvalidDataException extends RuntimeException {

    public InvalidDataException() {
        super();
    }

    public InvalidDataException(String message) {
        super(message);
    }

    public InvalidDataException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidDataException(Throwable cause) {
        super(cause);
    }

}
