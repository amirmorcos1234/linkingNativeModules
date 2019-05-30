package ro.vodafone.mcare.android.utils;

import android.util.Log;

import java.util.List;

/**
 * Created by bogdan.marica on 2/26/2017.
 * <p>
 * Debug class
 * Overrides Log.w/i/d.. commands and shows logcat output with URLs to the line where the command was written
 * <p>
 * can be used with message parameter, or without.
 * displays full path of calling method
 * <p>
 * default tag is 'Debug'
 */

public class D {

    public static void w() {
        StackTraceElement[] stackTraceElement = Thread.currentThread()
                .getStackTrace();
        int currentIndex = -1;
        for (int i = 0; i < stackTraceElement.length; i++) {
            if (stackTraceElement[i].getMethodName().compareTo("w") == 0) {
                currentIndex = i + 1;
                break;
            }
        }

        String fullClassName = stackTraceElement[currentIndex].getClassName();
        String className = stackTraceElement[currentIndex].getClassName().substring(stackTraceElement[currentIndex].getClassName().lastIndexOf(".") + 1);
        String methodName = stackTraceElement[currentIndex].getMethodName();

        String lineNumber = String
                .valueOf(stackTraceElement[currentIndex].getLineNumber());


        Log.w("Debug", "at " + fullClassName + "." + methodName + "(" + className + ".java:" + lineNumber + ")" + "");

    }

    public static void e() {
        StackTraceElement[] stackTraceElement = Thread.currentThread()
                .getStackTrace();
        int currentIndex = -1;
        for (int i = 0; i < stackTraceElement.length; i++) {
            if (stackTraceElement[i].getMethodName().compareTo("e") == 0) {
                currentIndex = i + 1;
                break;
            }
        }

        String fullClassName = stackTraceElement[currentIndex].getClassName();
        String className = stackTraceElement[currentIndex].getClassName().substring(stackTraceElement[currentIndex].getClassName().lastIndexOf(".") + 1);
        String methodName = stackTraceElement[currentIndex].getMethodName();

        String lineNumber = String
                .valueOf(stackTraceElement[currentIndex].getLineNumber());


        Log.e("Debug", "at " + fullClassName + "." + methodName + "(" + className + ".java:" + lineNumber + ")" + "");

    }

    public static void i() {

        StackTraceElement[] stackTraceElement = Thread.currentThread()
                .getStackTrace();
        int currentIndex = -1;
        for (int i = 0; i < stackTraceElement.length; i++) {
            if (stackTraceElement[i].getMethodName().compareTo("i") == 0) {
                currentIndex = i + 1;
                break;
            }
        }

        String fullClassName = stackTraceElement[currentIndex].getClassName();
        String className = stackTraceElement[currentIndex].getClassName().substring(stackTraceElement[currentIndex].getClassName().lastIndexOf(".") + 1);
        String methodName = stackTraceElement[currentIndex].getMethodName();

        String lineNumber = String
                .valueOf(stackTraceElement[currentIndex].getLineNumber());


        Log.i("Debug", "at " + fullClassName + "." + methodName + "(" + className + ".java:" + lineNumber + ")" + "");

    }

    public static void d() {

        StackTraceElement[] stackTraceElement = Thread.currentThread()
                .getStackTrace();
        int currentIndex = -1;
        for (int i = 0; i < stackTraceElement.length; i++) {
            if (stackTraceElement[i].getMethodName().compareTo("d") == 0) {
                currentIndex = i + 1;
                break;
            }
        }

        String fullClassName = stackTraceElement[currentIndex].getClassName();
        String className = stackTraceElement[currentIndex].getClassName().substring(stackTraceElement[currentIndex].getClassName().lastIndexOf(".") + 1);
        String methodName = stackTraceElement[currentIndex].getMethodName();

        String lineNumber = String
                .valueOf(stackTraceElement[currentIndex].getLineNumber());


        Log.d("Debug", "at " + fullClassName + "." + methodName + "(" + className + ".java:" + lineNumber + ")" + "");

    }

    public static void v() {
        StackTraceElement[] stackTraceElement = Thread.currentThread()
                .getStackTrace();
        int currentIndex = -1;
        for (int i = 0; i < stackTraceElement.length; i++) {
            if (stackTraceElement[i].getMethodName().compareTo("v") == 0) {
                currentIndex = i + 1;
                break;
            }
        }

        String fullClassName = stackTraceElement[currentIndex].getClassName();
        String className = stackTraceElement[currentIndex].getClassName().substring(stackTraceElement[currentIndex].getClassName().lastIndexOf(".") + 1);
        String methodName = stackTraceElement[currentIndex].getMethodName();

        String lineNumber = String
                .valueOf(stackTraceElement[currentIndex].getLineNumber());

        Log.v("Debug", "at " + fullClassName + "." + methodName + "(" + className + ".java:" + lineNumber + ")" + "");
    }

    public static void w(String msg) {

        StackTraceElement[] stackTraceElement = Thread.currentThread()
                .getStackTrace();
        int currentIndex = -1;
        for (int i = 0; i < stackTraceElement.length; i++) {
            if (stackTraceElement[i].getMethodName().compareTo("w") == 0) {
                currentIndex = i + 1;
                break;
            }
        }

        String fullClassName = stackTraceElement[currentIndex].getClassName();
        String className = stackTraceElement[currentIndex].getClassName().substring(stackTraceElement[currentIndex].getClassName().lastIndexOf(".") + 1);
        String methodName = stackTraceElement[currentIndex].getMethodName();

        String lineNumber = String
                .valueOf(stackTraceElement[currentIndex].getLineNumber());


        Log.w("Debug", "at " + fullClassName + "." + methodName + "(" + className + ".java:" + lineNumber + ")" + " : " + msg);
    }

    public static void e(String msg) {

        StackTraceElement[] stackTraceElement = Thread.currentThread()
                .getStackTrace();
        int currentIndex = -1;
        for (int i = 0; i < stackTraceElement.length; i++) {
            if (stackTraceElement[i].getMethodName().compareTo("e") == 0) {
                currentIndex = i + 1;
                break;
            }
        }

        String fullClassName = stackTraceElement[currentIndex].getClassName();
        String className = stackTraceElement[currentIndex].getClassName().substring(stackTraceElement[currentIndex].getClassName().lastIndexOf(".") + 1);
        String methodName = stackTraceElement[currentIndex].getMethodName();

        String lineNumber = String
                .valueOf(stackTraceElement[currentIndex].getLineNumber());


        Log.e("Debug", "at " + fullClassName + "." + methodName + "(" + className + ".java:" + lineNumber + ")" + " : " + msg);
    }

    public static void i(String msg) {

        StackTraceElement[] stackTraceElement = Thread.currentThread()
                .getStackTrace();
        int currentIndex = -1;
        for (int i = 0; i < stackTraceElement.length; i++) {
            if (stackTraceElement[i].getMethodName().compareTo("i") == 0) {
                currentIndex = i + 1;
                break;
            }
        }

        String fullClassName = stackTraceElement[currentIndex].getClassName();
        String className = stackTraceElement[currentIndex].getClassName().substring(stackTraceElement[currentIndex].getClassName().lastIndexOf(".") + 1);
        String methodName = stackTraceElement[currentIndex].getMethodName();

        String lineNumber = String
                .valueOf(stackTraceElement[currentIndex].getLineNumber());


        Log.i("Debug", "at " + fullClassName + "." + methodName + "(" + className + ".java:" + lineNumber + ")" + " : " + msg);
    }

    public static void d(String msg) {

        StackTraceElement[] stackTraceElement = Thread.currentThread()
                .getStackTrace();
        int currentIndex = -1;
        for (int i = 0; i < stackTraceElement.length; i++) {
            if (stackTraceElement[i].getMethodName().compareTo("d") == 0) {
                currentIndex = i + 1;
                break;
            }
        }

        String fullClassName = stackTraceElement[currentIndex].getClassName();
        String className = stackTraceElement[currentIndex].getClassName().substring(stackTraceElement[currentIndex].getClassName().lastIndexOf(".") + 1);
        String methodName = stackTraceElement[currentIndex].getMethodName();

        String lineNumber = String
                .valueOf(stackTraceElement[currentIndex].getLineNumber());


        Log.d("Debug", "at " + fullClassName + "." + methodName + "(" + className + ".java:" + lineNumber + ")" + " : " + msg);
    }

    public static void v(String msg) {

        StackTraceElement[] stackTraceElement = Thread.currentThread()
                .getStackTrace();
        int currentIndex = -1;
        for (int i = 0; i < stackTraceElement.length; i++) {
            if (stackTraceElement[i].getMethodName().compareTo("v") == 0) {
                currentIndex = i + 1;
                break;
            }
        }

        String fullClassName = stackTraceElement[currentIndex].getClassName();
        String className = stackTraceElement[currentIndex].getClassName().substring(stackTraceElement[currentIndex].getClassName().lastIndexOf(".") + 1);
        String methodName = stackTraceElement[currentIndex].getMethodName();

        String lineNumber = String
                .valueOf(stackTraceElement[currentIndex].getLineNumber());


        Log.v("Debug", "at " + fullClassName + "." + methodName + "(" + className + ".java:" + lineNumber + ")" + " : " + msg);
    }

    public static void printCallers() {

        StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
        //
        Log.d("Debug", " \n\n\n");

        for (int i = 3; i < stackTraceElements.length; i++)
            Log.d("Debug", "" + stackTraceElements[i]);

        Log.d("Debug", " \n\n\n");
    }

    public static void showList(List list) {

        StackTraceElement[] stackTraceElement = Thread.currentThread()
                .getStackTrace();
        int currentIndex = -1;
        for (int i = 0; i < stackTraceElement.length; i++) {
            if (stackTraceElement[i].getMethodName().compareTo("showList") == 0) {
                currentIndex = i + 1;
                break;
            }
        }

        String fullClassName = stackTraceElement[currentIndex].getClassName();
        String className = stackTraceElement[currentIndex].getClassName().substring(stackTraceElement[currentIndex].getClassName().lastIndexOf(".") + 1);
        String methodName = stackTraceElement[currentIndex].getMethodName();

        String lineNumber = String
                .valueOf(stackTraceElement[currentIndex].getLineNumber());

        if (list != null) {
            String s = "";
            for (int i = 0; i < list.size(); i++) {
                s += "  \n<<- " + list.get(i) + " ->>\n   ";
            }
            Log.w("Debug", "at " + fullClassName + "." + methodName + "(" + className + ".java:" + lineNumber + ")" + " : " + "\n\n\n\n\n");
            Log.w("Debug", "at " + fullClassName + "." + methodName + "(" + className + ".java:" + lineNumber + ")" + " : " + s);
            Log.w("Debug", "at " + fullClassName + "." + methodName + "(" + className + ".java:" + lineNumber + ")" + " : " + "\n\n\n\n\n");
        } else {
            Log.w("Debug", "at " + fullClassName + "." + methodName + "(" + className + ".java:" + lineNumber + ")" + " : " + "\n\n\n\n\n");
            Log.w("Debug", "at " + fullClassName + "." + methodName + "(" + className + ".java:" + lineNumber + ")" + " : " + "NULL LIST");
            Log.w("Debug", "at " + fullClassName + "." + methodName + "(" + className + ".java:" + lineNumber + ")" + " : " + "\n\n\n\n\n");
        }
    }
}
