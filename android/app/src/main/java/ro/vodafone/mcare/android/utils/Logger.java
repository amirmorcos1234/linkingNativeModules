package ro.vodafone.mcare.android.utils;

import android.util.Log;

import ro.vodafone.mcare.android.application.VodafoneController;

/**
 * Defines the logging options of the application.
 *
 * @author maria.gaspar
 */
public class Logger {

    private String className;

    /**
     * Initializes the ConsoleLogger.
     *
     * @param clazz the class using the logger.
     */
    Logger(Class<?> clazz) {
        this.className = clazz.getSimpleName();
    }

    public void d(String message) {
        if (VodafoneController.getInstance().isDebugEnabled()) {
            Log.d(className, message);
        }
    }

    public void v(String message) {
        Log.v(className, message);
    }

    public void i(String message) {
        Log.i(className, message);
    }

    public void w(String message) {
        Log.w(className, message);
    }

    public void e(String message) {
        Log.e(className, message);
    }

    public void d(String message, Throwable throwable) {
        if (VodafoneController.getInstance().isDebugEnabled()) {
            Log.d(className, message, throwable);
        }
    }

    public void e(String message, Throwable throwable) {
        Log.e(className, message, throwable);
    }

    public void w(String message, Throwable throwable) {
        Log.w(className, message, throwable);
    }

    /**
     * Creates a logger instance.
     *
     * @param clazz the class where the logger is defined.
     * @return the created instance.
     */
    public static Logger getInstance(Class<?> clazz) {
        return new Logger(clazz);
    }

}
