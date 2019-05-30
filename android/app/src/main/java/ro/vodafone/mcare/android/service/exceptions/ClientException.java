package ro.vodafone.mcare.android.service.exceptions;

/**
 * Class handling exceptions thrown by the Vodafone client.
 * 
 * @author maria.gaspar
 * 
 */
public class ClientException extends Exception {

  private static final long serialVersionUID = 5575632729253910632L;

  public ClientException() {
    super();
  }

  public ClientException(String detailMessage, Throwable throwable) {
    super(detailMessage, throwable);
  }

  public ClientException(Throwable throwable) {
    super(throwable);
  }

  public ClientException(String message) {
    super(message);
  }
}