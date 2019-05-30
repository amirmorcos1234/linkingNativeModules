package ro.vodafone.mcare.android.ui.utils;

import android.app.Activity;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;


public class KeyboardHelper {

    public static void hideKeyboard(Activity activity) {
        if (activity == null)
            return;

        InputMethodManager imm = (InputMethodManager) activity.getSystemService(activity.INPUT_METHOD_SERVICE);
        View focus = activity.getCurrentFocus();
        if (null != focus && null != focus.getWindowToken() && EditText.class.isAssignableFrom(focus.getClass()))
            imm.hideSoftInputFromWindow(focus.getWindowToken(), 0);
        else
            activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

    }
}
