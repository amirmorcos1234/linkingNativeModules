package ro.vodafone.mcare.android.service.exceptions;

/**
 * Exception that will be thrown in case of an error with the device's internet connection.
 * 
 * @author maria.gaspar
 * 
 */
public class InternetConnectionProblemException extends ClientException {

  private static final long serialVersionUID = 4075781159140794809L;

  public InternetConnectionProblemException() {
    super();
  }

  public InternetConnectionProblemException(String detailMessage, Throwable throwable) {
    super(detailMessage, throwable);
  }

  public InternetConnectionProblemException(Throwable throwable) {
    super(throwable);
  }

  public InternetConnectionProblemException(String message) {
    super(message);
  }

}