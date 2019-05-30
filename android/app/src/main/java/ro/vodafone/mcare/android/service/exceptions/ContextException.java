package ro.vodafone.mcare.android.service.exceptions;

import android.os.Build;
import android.support.annotation.RequiresApi;

/**
 * Created by Serban Radulescu on 11/16/2017.
 */

public class ContextException extends Exception {
    public ContextException() {
        super();
    }

    public ContextException(String message) {
        super(message);
    }

    public ContextException(String message, Throwable cause) {
        super(message, cause);
    }

    public ContextException(Throwable cause) {
        super(cause);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    protected ContextException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
