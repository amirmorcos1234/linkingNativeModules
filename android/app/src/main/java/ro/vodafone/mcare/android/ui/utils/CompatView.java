package ro.vodafone.mcare.android.ui.utils;

import android.os.Build;
import android.view.View;

/**
 * Created by Victor Radulescu on 2/8/2017.
 */

public class CompatView {

    public static void bringToFront(View view){
        view.bringToFront();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            view.setZ(10000);
        }
        view.invalidate();
    }
}
