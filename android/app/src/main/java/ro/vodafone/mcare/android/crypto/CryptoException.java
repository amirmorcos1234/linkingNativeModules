package ro.vodafone.mcare.android.crypto;

/**
 * Exception for actions related to cryptography.
 *
 * @author maria.gaspar
 */
public class CryptoException extends Exception {

    private static final long serialVersionUID = 8047399467628711094L;

    /**
     * Creates a new CryptoException by providing its cause.
     *
     * @param cause the cause of exception
     */
    public CryptoException(Throwable cause) {
        super(cause);
    }

}
