package ro.vodafone.mcare.android.service.exceptions;

/**
 * Exception created in case of an error when performing the authentication operation.
 *
 * @author maria.gaspar
 *
 */
public class AuthenticationServiceException extends Exception {

    private static final long serialVersionUID = -4337231219939341943L;
    /**
     * The id of an exception message defined in the strings.xml.
     */
    private int messageId;

    public AuthenticationServiceException(String message) {
        super(message);
    }

    public AuthenticationServiceException() {
        super();
    }

    public AuthenticationServiceException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }

    public AuthenticationServiceException(Throwable throwable) {
        super(throwable);
    }

    public AuthenticationServiceException(int messageId) {
        this.messageId = messageId;
    }

    public AuthenticationServiceException(int messageId, Throwable throwable) {
        super(throwable);
        this.messageId = messageId;
    }

    public int getMessageId() {
        return messageId;
    }

}