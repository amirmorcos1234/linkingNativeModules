package ro.vodafone.mcare.android.utils;


public class TrackingException extends Exception {
    public static final String CHANGE_DETACHED_FRAGMENT_VIEWS = "Attempt to change detached fragment views. This might cause missed updates on the UI";
    public static final String NULL_FRAGMENT_CONTEXT = "Attempt to use fragment context when it is null (either before attach or detach from activity). This might cause missed updates on the UI";

    public TrackingException() {
    }

    public TrackingException(String message){
        super(message);
    }

    public TrackingException(Throwable cause){
        super(cause);
    }

    public TrackingException(String message, Throwable cause) {
        super(message, cause);
    }
}
