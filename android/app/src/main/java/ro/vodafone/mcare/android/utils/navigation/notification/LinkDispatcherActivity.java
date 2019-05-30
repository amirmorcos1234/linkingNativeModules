package ro.vodafone.mcare.android.utils.navigation.notification;

import android.os.Bundle;
import android.util.Log;

import ro.vodafone.mcare.android.BuildConfig;
import ro.vodafone.mcare.android.application.controllers.UserDataController;
import ro.vodafone.mcare.android.ui.activities.BaseActivity;

/**
 * Created by Bivol Pavel on 25.04.2018.
 *
 * This class can be used now only for pages where user != null.
 */

public class LinkDispatcherActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            if (getIntent() != null && getIntent().getData() != null) {
                //Set here navigation action on UserDataController and then run this action on finally block.
                DeepLinkDispatcher.dispatchAction(getIntent().getData());
            }
        } catch (IllegalArgumentException iae) {
            // Malformed URL
            if (BuildConfig.DEBUG) {
                Log.e("Deep links", "Invalid URI", iae);
            }
        } finally {
            UserDataController.getInstance().startDashboardAction(this);
            // Always finish the activity so that it doesn't stay in our history
            finish();
        }
    }
}